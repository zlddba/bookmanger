package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.SaleRecord
import org.zl.team.mapper.BookMapper
import org.zl.team.mapper.MemberMapper
import org.zl.team.mapper.MemberPolicyMapper
import org.zl.team.mapper.SaleRecordMapper

object SaleService {

    fun listAll(): List<SaleRecord> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(SaleRecordMapper::class.java).selectAll() }
        finally { session.close() }
    }

    fun listByDate(date: String): List<SaleRecord> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(SaleRecordMapper::class.java).selectByDate(date) }
        finally { session.close() }
    }

    fun listByDateRange(startDate: String, endDate: String): List<SaleRecord> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(SaleRecordMapper::class.java).selectByDateRange(startDate, endDate) }
        finally { session.close() }
    }

    fun listByMember(cardNo: String): List<SaleRecord> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(SaleRecordMapper::class.java).selectByMember(cardNo) }
        finally { session.close() }
    }

    fun sell(bookId: String, quantity: Int, cardNo: String?, date: String, remark: String?): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val saleMapper = session.getMapper(SaleRecordMapper::class.java)
            val bookMapper = session.getMapper(BookMapper::class.java)
            val memberMapper = session.getMapper(MemberMapper::class.java)
            val policyMapper = session.getMapper(MemberPolicyMapper::class.java)

            val book = bookMapper.selectById(bookId) ?: return false
            if (book.stock < quantity) return false

            val price = book.price ?: 0.0

            // 计算折扣
            var discount = 1.0
            if (cardNo != null && cardNo.isNotEmpty()) {
                val member = memberMapper.selectById(cardNo) ?: return false
                val policy = policyMapper.selectByLevel(member.level)
                discount = policy?.discount?.toDoubleOrNull() ?: 1.0
            }

            val amount = quantity * price * discount

            if (remark != null && remark.isNotEmpty())
                saleMapper.insert(SaleRecord(0, bookId, quantity, cardNo, discount, amount, date, remark))
            else
                saleMapper.insert(SaleRecord(0, bookId, quantity, cardNo, discount, amount, date, null))

            // 扣减库存
            bookMapper.updateStock(bookId, book.stock - quantity)

            // 会员累计消费额更新 + 等级自动升级
            if (cardNo != null && cardNo.isNotEmpty()) {
                val member = memberMapper.selectById(cardNo)
                if (member != null) {
                    val newTotal = member.totalSpent + amount
                    memberMapper.updateTotalSpent(cardNo, newTotal)

                    // 检查升级：找到满足条件的最高等级
                    val allPolicies = policyMapper.selectAll()
                    val newLevel = allPolicies.filter { (it.minAmount ?: 0) <= newTotal.toInt() }
                        .maxOfOrNull { it.level } ?: member.level
                    if (newLevel > member.level) {
                        memberMapper.updateLevel(cardNo, newLevel)
                    }
                }
            }

            session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }
}
