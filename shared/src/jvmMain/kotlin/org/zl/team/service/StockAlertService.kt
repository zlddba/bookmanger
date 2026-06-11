package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.Book

object StockAlertService {

    data class StockAlert(
        val book: Book,
        val threshold: Int
    )

    fun getLowStockBooks(threshold: Int = SystemConfig.getStockAlertThreshold()): List<StockAlert> {
        val session = MyBatisUtil.getSqlSession()
        try {
            val all = session.getMapper(org.zl.team.mapper.BookMapper::class.java).selectAll()
            return all.filter { it.stock <= threshold && it.stock >= 0 }
                .map { StockAlert(it, threshold) }
                .sortedBy { it.book.stock }
        } finally {
            session.close()
        }
    }

    fun getCriticalStockBooks(): List<StockAlert> {
        return getLowStockBooks(SystemConfig.getCriticalStockThreshold())
    }

    fun getLowStockCount(threshold: Int = SystemConfig.getStockAlertThreshold()): Int {
        return getLowStockBooks(threshold).size
    }

    fun getCriticalStockCount(): Int {
        return getLowStockBooks(SystemConfig.getCriticalStockThreshold()).size
    }
}
