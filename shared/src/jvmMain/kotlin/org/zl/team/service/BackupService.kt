package org.zl.team.service

import org.zl.team.config.DatabaseInitializer
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupService {

    private val backupDir: Path = DatabaseInitializer.dataDir.resolve("backups")

    data class BackupInfo(
        val fileName: String,
        val fileSize: Long,
        val createdAt: String
    )

    fun listBackups(): List<BackupInfo> {
        Files.createDirectories(backupDir)
        return Files.list(backupDir).use { stream ->
            stream.filter { it.toString().endsWith(".zip") }
                .map { path ->
                    val file = path.toFile()
                    BackupInfo(
                        fileName = file.name,
                        fileSize = file.length(),
                        createdAt = file.lastModified().let {
                            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(it))
                        }
                    )
                }
                .toList()
                .sortedByDescending { it.createdAt }
        }
    }

    fun createBackup(): String {
        Files.createDirectories(backupDir)
        val dbPath = DatabaseInitializer.getDbPath()
        val dbFile = File(dbPath)
        if (!dbFile.exists()) throw IllegalStateException("数据库文件不存在: $dbPath")

        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val zipFile = backupDir.resolve("backup_$timestamp.zip").toFile()

        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            // 备份数据库文件
            zos.putNextEntry(ZipEntry("bookstore.db"))
            FileInputStream(dbFile).use { fis -> fis.copyTo(zos) }
            zos.closeEntry()
        }

        OperationLogService.log("备份", "系统", detail = "数据库备份: ${zipFile.name}")
        return zipFile.absolutePath
    }

    fun restoreBackup(fileName: String) {
        val zipFile = backupDir.resolve(fileName).toFile()
        if (!zipFile.exists()) throw IllegalStateException("备份文件不存在: $fileName")

        val dbPath = DatabaseInitializer.getDbPath()

        ZipInputStream(FileInputStream(zipFile)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (entry.name == "bookstore.db") {
                    FileOutputStream(dbPath).use { fos -> zis.copyTo(fos) }
                }
                entry = zis.nextEntry
            }
        }

        OperationLogService.log("恢复", "系统", detail = "数据库恢复: $fileName")
    }

    fun deleteBackup(fileName: String) {
        val file = backupDir.resolve(fileName).toFile()
        if (file.exists()) file.delete()
    }

    fun autoBackup() {
        try {
            createBackup()
            val maxRetention = SystemConfig.getBackupRetentionCount()
            val all = listBackups()
            if (all.size > maxRetention) {
                all.drop(maxRetention).forEach { deleteBackup(it.fileName) }
            }
        } catch (_: Exception) { }
    }
}
