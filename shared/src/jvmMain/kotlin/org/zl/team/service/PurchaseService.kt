package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.PurchaseRecord
import org.zl.team.mapper.BookMapper
import org.zl.team.mapper.BookPriceMapper
import org.zl.team.mapper.PurchaseRecordMapper
import org.zl.team.entity.BookPrice

object PurchaseService {

    fun listAll(): List<PurchaseRecord> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(PurchaseRecordMapper::class.java).selectAll() }
        finally { session.close() }
    }

    fun listByDateRange(start: String, end: String): List<PurchaseRecord> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(PurchaseRecordMapper::class.java).selectByDateRange(start, end) }
        finally { session.close() }
    }

    fun register(supplierId: String, bookId: String, quantity: Int,
                 unitPrice: Double, discount: Double, date: String, remark: String?): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val purchaseMapper = session.getMapper(PurchaseRecordMapper::class.java)
            val bookMapper = session.getMapper(BookMapper::class.java)
            val priceMapper = session.getMapper(BookPriceMapper::class.java)

            val book = bookMapper.selectById(bookId) ?: return false
            val amount = quantity * unitPrice * discount

            // 写入进书记录
            if (remark != null && remark.isNotEmpty())
                purchaseMapper.insert(PurchaseRecord(0, supplierId, bookId, quantity, unitPrice, discount, amount, date, remark))
            else
                purchaseMapper.insert(PurchaseRecord(0, supplierId, bookId, quantity, unitPrice, discount, amount, date, null))

            // 更新库存
            bookMapper.updateStock(bookId, book.stock + quantity)

            // 更新/插入进价
            val existingPrice = priceMapper.selectById(bookId)
            if (existingPrice != null)
                priceMapper.update(BookPrice(bookId, unitPrice, date))
            else
                priceMapper.insert(BookPrice(bookId, unitPrice, date))

            session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }
}
