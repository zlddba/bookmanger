import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material3:material3:1.9.0")
    implementation("org.jetbrains.compose.foundation:foundation:1.11.1")
    implementation("org.jetbrains.compose.ui:ui:1.11.1")
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
    implementation("org.slf4j:slf4j-api:2.0.17")
}

val jreDir = when {
    System.getProperty("os.name").lowercase().contains("win") ->
        rootProject.file("jrerun/windows/jre")

    System.getProperty("os.name").lowercase().contains("linux") ->
        rootProject.file("jrerun/linux/jre")

    else -> null
}

// 获取 JDK 中的 jpackage 命令路径（优先使用 JAVA_HOME 环境变量）
fun findJpackage(): String {
    val candidates = listOfNotNull(
        System.getenv("JAVA_HOME"),
        System.getenv("JDK_HOME"),
        System.getProperty("java.home")
    ).flatMap { home ->
        listOf(
            File(home, "bin/jpackage.exe"),
            File(home, "bin/jpackage"),
            File(home, "../bin/jpackage.exe"),
            File(home, "../bin/jpackage")
        )
    }
    val jpackage = candidates.firstOrNull { it.exists() }
        ?: error("未找到 jpackage（已搜索 JAVA_HOME/JDK_HOME/java.home），请确保使用完整 JDK")
    return jpackage.absolutePath
}

compose.desktop {
    application {
        mainClass = "org.zl.team.MainKt"

        jvmArgs(
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-opens=java.base/java.text=ALL-UNNAMED",
            "--add-opens=java.desktop/java.awt=ALL-UNNAMED"
        )

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "BookManager"
            packageVersion = "1.0.0"
            description = "Book Manager System"
            vendor = "BookManager"

            windows {
                menu = true
                shortcut = true
            }
        }

        buildTypes {
            release {
                proguard {
                    configurationFiles.from(project.file("proguard-rules.pro"))
                }
            }
        }
    }
}

// ── 自定义打包任务：带完整 JRE ──────────────────────────
val packageWithJre by tasks.registering(Exec::class) {
    description = "使用 jpackage + 完整 JRE 打包为 MSI 安装包"
    group = "distribution"

    dependsOn("createDistributable")

    // 配置缓存不兼容：doFirst 中引用外部变量
    notCompatibleWithConfigurationCache("uses jreDir/jpackage resolved at execution time")

    doFirst {
        // ── WiX 工具链 ────────────────────────────────────────
        val wixDir = File(System.getenv("GRADLE_USER_HOME") ?: "${System.getProperty("user.home")}/.gradle", "compose-jb/wix311")
        if (wixDir.exists()) {
            environment("PATH", "${wixDir.absolutePath}${File.pathSeparator}${System.getenv("PATH")}")
            logger.lifecycle("WiX 工具链: ${wixDir.absolutePath}")
        } else {
            logger.warn("WiX 未找到，MSI 打包将失败: $wixDir")
        }

        // ── 清理 jpackage 临时文件（防止 FileAlreadyExistsException） ──
        val tmpDir = File(System.getProperty("java.io.tmpdir"))
        tmpDir.listFiles { f -> f.name.startsWith("jdk.jpackage") }?.forEach { f ->
            f.deleteRecursively()
            logger.lifecycle("清理 jpackage 临时目录: ${f.name}")
        }

        val jpackage = findJpackage()
        logger.lifecycle("jpackage: $jpackage")
        logger.lifecycle("JAVA_HOME: ${System.getenv("JAVA_HOME") ?: "(not set)"}")

        val pkgName = "BookManager"
        val outputDir = layout.buildDirectory.dir("jpackage").get().asFile

        // ── 查找 JAR 目录并创建干净的临时输入目录 ──
        val distRoot = layout.buildDirectory.dir("compose/binaries/main/app").get().asFile
        val appDir = distRoot.listFiles()
            ?.firstOrNull { it.isDirectory }
            ?.let { File(it, "app") }
            ?: throw GradleException("未找到分发目录，请先执行 createDistributable（查找位置: $distRoot）")

        val mainJar = appDir.listFiles()
            ?.firstOrNull { it.name.startsWith("desktopApp-") && it.name.endsWith(".jar") }
            ?: throw GradleException("未在 $appDir 中找到主 JAR（desktopApp-*.jar）")

        // jpackage 的 --input 目录不能包含 .cfg 等文件，否则临时目录中会冲突
        // 创建一个干净的暂存目录，复制所有运行时需要的文件
        val stagingDir = layout.buildDirectory.dir("jpackage/staging").get().asFile
        stagingDir.mkdirs()
        stagingDir.listFiles()?.forEach { it.delete() }
        appDir.listFiles()?.forEach { f ->
            if (f.name.endsWith(".cfg") || f.name == ".jpackage.xml") return@forEach
            if (f.isDirectory) {
                if (f.listFiles()?.isNotEmpty() == true)
                    f.copyRecursively(File(stagingDir, f.name), overwrite = true)
            } else {
                f.copyTo(File(stagingDir, f.name), overwrite = true)
            }
        }
        logger.lifecycle("JAR 暂存目录: $stagingDir (${stagingDir.listFiles()?.size} 个文件)")

        outputDir.mkdirs()

        val args = mutableListOf(
            jpackage,
            "--type", "msi",
            "--input", stagingDir.absolutePath,
            "--main-jar", mainJar.name,
            "--main-class", "org.zl.team.MainKt",
            "--name", pkgName,
            "--app-version", "1.0.0",
            "--vendor", "BookManager",
            "--dest", outputDir.absolutePath,
            "--win-menu",
            "--win-shortcut",
            "--win-dir-chooser",
            "--java-options", "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--java-options", "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--java-options", "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
            "--java-options", "--add-opens=java.base/java.text=ALL-UNNAMED",
            "--java-options", "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
            "--java-options", "-Dskiko.library.path=\$APPDIR"
        )
        if (jreDir != null && jreDir.exists()) {
            args.add("--runtime-image")
            args.add(jreDir.absolutePath)
            logger.lifecycle("使用自定义 JRE: ${jreDir.absolutePath}")
        } else {
            logger.warn("JRE 目录未配置或不存在（${jreDir?.absolutePath}），将使用系统 JRE")
        }
        commandLine = args
    }
}