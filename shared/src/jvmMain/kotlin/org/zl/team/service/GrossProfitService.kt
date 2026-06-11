package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.mapper.BookPriceMapper
import org.zl.team.mapper.SaleRecordMapper

object GrossProfitService {

    data class GrossProfitRow(
        val date: String,
        val bookId: String,
        val bookTitle: String,
        val quantity: Int,
        val unitPrice: Double,
        val saleAmount: Double,
        val costPrice: Double,
        val cost: Double,
        val grossProfit: Double
    )

    fun query(startDate: String, endDate: String): List<GrossProfitRow> {
        val session = MyBatisUtil.getSqlSession()
        try {
            val saleMapper = session.getMapper(SaleRecordMapper::class.java)
            val priceMapper = session.getMapper(BookPriceMapper::class.java)
            val bookMap = BookService.listAll().associate { it.bookId to it.title }

            return saleMapper.selectByDateRange(startDate, endDate)
                .mapNotNull { s ->
                    if (s.date == null || s.amount == null || s.bookId == null || s.quantity == null) null
                    else {
                        val bp = priceMapper.selectById(s.bookId)
                        val costPrice = bp?.price ?: 0.0
                        val cost = s.quantity * costPrice
                        val profit = s.amount - cost

                        GrossProfitRow(
                            date = s.date,
                            bookId = s.bookId,
                            bookTitle = bookMap[s.bookId] ?: s.bookId,
                            quantity = s.quantity,
                            unitPrice = s.amount / s.quantity,
                            saleAmount = s.amount,
                            costPrice = costPrice,
                            cost = cost,
                            grossProfit = profit
                        )
                    }
                }
                .sortedByDescending { it.date }
        } finally {
            session.close()
        }
    }
}
