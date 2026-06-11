package org.zl.team.util

import org.zl.team.entity.Book
import java.awt.Font
import java.awt.Graphics
import java.awt.print.PageFormat
import java.awt.print.Printable
import java.awt.print.PrinterJob

object ReceiptPrinter {

    data class ReceiptItem(
        val book: Book,
        val quantity: Int,
        val unitPrice: Double,
        val subtotal: Double
    )

    fun print(bookTitle: String, quantity: Int, unitPrice: Double, total: Double, date: String) {
        Thread {
            val job = PrinterJob.getPrinterJob()
            if (!job.printDialog()) return@Thread

            job.setPrintable(object : Printable {
                override fun print(g: Graphics, pf: PageFormat, pageIndex: Int): Int {
                    if (pageIndex > 0) return Printable.NO_SUCH_PAGE

                    val fontPlain = Font("Monospaced", Font.PLAIN, 10)
                    val fontBold = Font("Monospaced", Font.BOLD, 12)
                    val fontSmall = Font("Monospaced", Font.PLAIN, 9)
                    g.font = fontPlain

                    val x = 80
                    var y = 120
                    val lineH = 16

                    g.font = fontBold
                    g.drawString("图书管理系统 - 销售小票", x, y); y += lineH * 2

                    g.font = fontPlain
                    g.drawString("━".repeat(40), x, y); y += lineH

                    g.drawString("日期: $date", x, y); y += lineH
                    g.drawString("━".repeat(40), x, y); y += lineH + 4

                    g.font = fontSmall
                    g.drawString(String.format("%-30s %4s %8s", "书名", "数量", "金额"), x, y); y += lineH
                    g.drawString("─".repeat(40), x, y); y += lineH

                    g.font = fontPlain
                    val truncatedTitle = if (bookTitle.length > 24) bookTitle.take(23) + "…" else bookTitle
                    g.drawString(String.format("%-30s %4d %8.2f", truncatedTitle, quantity, total), x, y); y += lineH + 4

                    g.drawString("━".repeat(40), x, y); y += lineH + 4
                    g.font = fontBold
                    g.drawString(String.format("%-30s %4d %8.2f", "合计", quantity, total), x, y); y += lineH * 3

                    g.font = Font("Serif", Font.ITALIC, 9)
                    g.drawString("感谢您的光临！", x + 60, y); y += lineH

                    return Printable.PAGE_EXISTS
                }
            })

            try { job.print() } catch (_: Exception) { }
        }.start()
    }
}
