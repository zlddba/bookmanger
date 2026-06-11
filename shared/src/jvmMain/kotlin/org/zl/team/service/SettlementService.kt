package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.mapper.PurchaseRecordMapper
import org.zl.team.mapper.ReturnRecordMapper
import org.zl.team.mapper.SaleRecordMapper

object SettlementService {

    data class SettlementRow(
        val period: String,
        val saleAmount: Double,
        val saleCount: Int,
        val purchaseAmount: Double,
        val purchaseCount: Int,
        val returnAmount: Double,
        val returnCount: Int,
        val profit: Double
    )

    fun dailySettlement(startDate: String, endDate: String): List<SettlementRow> {
        return settlement(startDate, endDate) { d -> d }
    }

    fun monthlySettlement(startDate: String, endDate: String): List<SettlementRow> {
        return settlement(startDate, endDate) { d -> d.substring(0, 7) }
    }

    private fun settlement(startDate: String, endDate: String, format: (String) -> String): List<SettlementRow> {
        val session = MyBatisUtil.getSqlSession()
        try {
            val purchaseMapper = session.getMapper(PurchaseRecordMapper::class.java)
            val saleMapper = session.getMapper(SaleRecordMapper::class.java)
            val returnMapper = session.getMapper(ReturnRecordMapper::class.java)

            val purchases = purchaseMapper.selectByDateRange(startDate, endDate)
            val sales = saleMapper.selectByDateRange(startDate, endDate)
            val returns = returnMapper.selectByDateRange(startDate, endDate)

            val salesByPeriod = sales
                .filter { it.date != null && it.amount != null }
                .groupBy { format(it.date ?: "") }
            val purchasesByPeriod = purchases
                .filter { it.date != null && it.amount != null }
                .groupBy { format(it.date ?: "") }
            val returnsByPeriod = returns
                .filter { it.date != null && it.amount != null }
                .groupBy { format(it.date ?: "") }

            val allPeriods = (salesByPeriod.keys + purchasesByPeriod.keys + returnsByPeriod.keys).sorted()

            return allPeriods.map { period ->
                val saleList = salesByPeriod[period] ?: emptyList()
                val purchaseList = purchasesByPeriod[period] ?: emptyList()
                val returnList = returnsByPeriod[period] ?: emptyList()

                val saleAmt = saleList.sumOf { it.amount ?: 0.0 }
                val purchaseAmt = purchaseList.sumOf { it.amount ?: 0.0 }
                val returnAmt = returnList.sumOf { it.amount ?: 0.0 }

                SettlementRow(
                    period = period,
                    saleAmount = saleAmt,
                    saleCount = saleList.size,
                    purchaseAmount = purchaseAmt,
                    purchaseCount = purchaseList.size,
                    returnAmount = returnAmt,
                    returnCount = returnList.size,
                    profit = saleAmt - purchaseAmt + returnAmt
                )
            }
        } finally {
            session.close()
        }
    }
}
