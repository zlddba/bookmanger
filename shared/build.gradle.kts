plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm()
    
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        jvmMain.dependencies {
            // 数据库持久层（从 tsglxt 迁移）
            implementation("org.mybatis:mybatis:3.5.19")
            implementation("org.xerial:sqlite-jdbc:3.49.1.0")
            implementation("org.slf4j:slf4j-api:2.0.17")
            implementation("ch.qos.logback:logback-classic:1.5.32")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
            implementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
        }
    }
}