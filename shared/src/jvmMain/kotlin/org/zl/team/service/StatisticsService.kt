package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.mapper.PurchaseRecordMapper
import org.zl.team.mapper.ReturnRecordMapper
import org.zl.team.mapper.SaleRecordMapper

object StatisticsService {

    data class StatRow(
        val period: String,       // 时段标识（年/月/日）
        val purchaseAmount: Double, // 进货金额
        val saleAmount: Double,    // 销售金额
        val returnAmount: Double,  // 退货金额
        val profit: Double         // 利润 = 销售 - 进货 + 退货回收
    )

    fun statistics(startDate: String, endDate: String, groupBy: String): List<StatRow> {
        val session = MyBatisUtil.getSqlSession()
        try {
            val purchaseMapper = session.getMapper(PurchaseRecordMapper::class.java)
            val saleMapper = session.getMapper(SaleRecordMapper::class.java)
            val returnMapper = session.getMapper(ReturnRecordMapper::class.java)

            val purchases = purchaseMapper.selectByDateRange(startDate, endDate)
            val sales = saleMapper.selectByDateRange(startDate, endDate)
            val returns = returnMapper.selectByDateRange(startDate, endDate)

            val format = when (groupBy) {
                "年" -> { d: String -> d.substring(0, 4) }
                "月" -> { d: String -> d.substring(0, 7) }
                else -> { d: String -> d } // 日
            }

            val purchaseByPeriod = purchases
                .filter { it.date != null && it.amount != null }
                .groupBy { format(it.date ?: "") }
                .mapValues { e -> e.value.sumOf { it.amount ?: 0.0 } }

            val saleByPeriod = sales
                .filter { it.amount != null }
                .groupBy { format(it.date ?: "") }
                .mapValues { e -> e.value.sumOf { it.amount ?: 0.0 } }

            val returnByPeriod = returns
                .filter { it.date != null && it.amount != null }
                .groupBy { format(it.date ?: "") }
                .mapValues { e -> e.value.sumOf { it.amount ?: 0.0 } }

            val allPeriods = (purchaseByPeriod.keys + saleByPeriod.keys + returnByPeriod.keys).sorted()

            return allPeriods.map { period ->
                val purchaseAmt = purchaseByPeriod[period] ?: 0.0
                val saleAmt = saleByPeriod[period] ?: 0.0
                val returnAmt = returnByPeriod[period] ?: 0.0
                StatRow(period, purchaseAmt, saleAmt, returnAmt, saleAmt - purchaseAmt + returnAmt)
            }
        } finally {
            session.close()
        }
    }
}
