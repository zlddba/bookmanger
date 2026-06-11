package org.zl.team.util

import java.io.File
import java.io.FileWriter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

object CsvExporter {
    fun export(headers: List<String>, rows: List<List<String?>>, defaultName: String = "export.csv"): Boolean {
        val chooser = JFileChooser().apply {
            selectedFile = File(defaultName)
            dialogTitle = "导出 CSV"
            fileFilter = FileNameExtensionFilter("CSV 文件 (*.csv)", "csv")
        }
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return false

        val file = chooser.selectedFile.let {
            if (it.extension.lowercase() != "csv") File(it.absolutePath + ".csv") else it
        }

        FileWriter(file).use { writer ->
            // BOM for UTF-8 Excel compatibility
            writer.write("﻿")
            writer.write(headers.joinToString(",") { "\"${it.replace("\"", "\"\"")}\"" })
            writer.write("\n")
            rows.forEach { row ->
                writer.write(row.joinToString(",") { "\"${it?.replace("\"", "\"\"") ?: ""}\"" })
                writer.write("\n")
            }
        }
        return true
    }
}
