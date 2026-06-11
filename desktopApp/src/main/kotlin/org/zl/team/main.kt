package org.zl.team

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.slf4j.LoggerFactory
import org.zl.team.config.DatabaseInitializer
import java.io.File

fun main() {
    // 设置日志路径到用户目录（安装版的工作目录不可控）
    val logDir = File(System.getProperty("user.home"), ".bookmanager/logs")
    logDir.mkdirs()
    System.setProperty("bookmanager.log.dir", logDir.absolutePath)

    try {
        DatabaseInitializer.init()

        val log = LoggerFactory.getLogger("org.zl.team.MainKt")
        log.info("=== 图书管理系统启动 ===")
        log.info("日志目录: ${logDir.absolutePath}")
        log.info("工作目录: ${System.getProperty("user.dir")}")
        log.info("数据库: ${DatabaseInitializer::class.java.getResource("/bookstore.db") ?: "classpath 中未找到"}")

        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = "图书管理系统",
                state = rememberWindowState(placement = WindowPlacement.Maximized),
            ) {
                App()
            }
        }
    } catch (e: Throwable) {

        val crashLog = File(logDir, "crash.log")
        crashLog.writeText(
            """${java.time.LocalDateTime.now()} 启动失败:
${e::class.qualifiedName}: ${e.message}

${e.stackTrace.joinToString("\n") { "    $it" }}
"""
        )
        e.printStackTrace()
        throw e
    }
}
