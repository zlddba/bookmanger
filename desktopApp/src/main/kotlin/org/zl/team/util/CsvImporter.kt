package org.zl.team.util

import org.zl.team.entity.Book
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object CsvImporter {
    data class ImportResult(
        val successCount: Int,
        val failCount: Int,
        val errors: List<String> = emptyList()
    )

    fun importAndSave(onInsert: (Book) -> Boolean): ImportResult {
        val chooser = JFileChooser().apply {
            dialogTitle = "导入 CSV"
            fileFilter = FileNameExtensionFilter("CSV 文件 (*.csv)", "csv")
        }
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
            return ImportResult(0, 0)

        return importFromFile(chooser.selectedFile, onInsert)
    }

    fun importFromFile(file: File, onInsert: (Book) -> Boolean): ImportResult {
        val lines = file.readLines()
        if (lines.isEmpty()) return ImportResult(0, 0, listOf("文件为空"))

        val headers = parseCsvLine(lines.first())
        val required = listOf("bookId", "title")
        val missing = required.filter { it !in headers.map { h -> h.lowercase().trim() } }
        if (missing.isNotEmpty()) return ImportResult(0, 0, missing.map { "缺少必要列: $it" })

        val colMap = headers.mapIndexed { i, h -> h.lowercase().trim() to i }.toMap()
        val dataLines = lines.drop(1).filter { it.isNotBlank() }

        var success = 0
        var fail = 0
        val errors = mutableListOf<String>()

        dataLines.forEachIndexed { idx, line ->
            try {
                val cols = parseCsvLine(line)
                val bookId = cols.getOrNull(colMap["bookid"] ?: -1)?.trim() ?: ""
                val title = cols.getOrNull(colMap["title"] ?: -1)?.trim() ?: ""

                if (bookId.isBlank() || title.isBlank()) {
                    fail++; errors.add("第${idx + 2}行: bookId 或 title 为空"); return@forEachIndexed
                }

                val book = Book(
                    bookId = bookId,
                    title = title,
                    categoryId = cols.getOrNull(colMap["categoryid"] ?: -1)?.trim()?.ifBlank { null },
                    series = cols.getOrNull(colMap["series"] ?: -1)?.trim()?.ifBlank { null },
                    author = cols.getOrNull(colMap["author"] ?: -1)?.trim()?.ifBlank { null },
                    publisher = cols.getOrNull(colMap["publisher"] ?: -1)?.trim()?.ifBlank { null },
                    edition = cols.getOrNull(colMap["edition"] ?: -1)?.trim()?.ifBlank { null },
                    isbn = cols.getOrNull(colMap["isbn"] ?: -1)?.trim()?.ifBlank { null },
                    price = cols.getOrNull(colMap["price"] ?: -1)?.trim()?.toDoubleOrNull(),
                    stock = cols.getOrNull(colMap["stock"] ?: -1)?.trim()?.toIntOrNull() ?: 0,
                    description = cols.getOrNull(colMap["description"] ?: -1)?.trim()?.ifBlank { null },
                    keywords = cols.getOrNull(colMap["keywords"] ?: -1)?.trim()?.ifBlank { null },
                    publishDate = cols.getOrNull(colMap["publishdate"] ?: -1)?.trim()?.ifBlank { null },
                    createdAt = null
                )
                if (onInsert(book)) success++ else { fail++; errors.add("第${idx + 2}行: 编号 '$bookId' 已存在或创建失败") }
            } catch (e: Exception) {
                fail++; errors.add("第${idx + 2}行: ${e.message}")
            }
        }
        return ImportResult(success, fail, errors)
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        for (c in line) {
            when {
                c == '"' -> inQuotes = !inQuotes
                c == ',' && !inQuotes -> { result.add(current.toString()); current.clear() }
                else -> current.append(c)
            }
        }
        result.add(current.toString())
        return result
    }
}
