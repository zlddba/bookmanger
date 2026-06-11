package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.ReturnRecord
import org.zl.team.mapper.BookMapper
import org.zl.team.mapper.ReturnRecordMapper

object ReturnService {

    fun listAll(): List<ReturnRecord> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(ReturnRecordMapper::class.java).selectAll() }
        finally { session.close() }
    }

    fun listByDateRange(startDate: String, endDate: String): List<ReturnRecord> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(ReturnRecordMapper::class.java).selectByDateRange(startDate, endDate) }
        finally { session.close() }
    }

    fun register(supplierId: String, bookId: String, unitPrice: Double,
                 quantity: Int, reason: String, date: String, remark: String?): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val returnMapper = session.getMapper(ReturnRecordMapper::class.java)
            val bookMapper = session.getMapper(BookMapper::class.java)

            val book = bookMapper.selectById(bookId) ?: return false
            if (book.stock < quantity) return false  // 库存不足

            val amount = quantity * unitPrice
            if (remark != null && remark.isNotEmpty())
                returnMapper.insert(ReturnRecord(0, supplierId, bookId, unitPrice, quantity, amount, reason, date, remark))
            else
                returnMapper.insert(ReturnRecord(0, supplierId, bookId, unitPrice, quantity, amount, reason, date, null))

            // 扣减库存
            bookMapper.updateStock(bookId, book.stock - quantity)

            session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }
}
