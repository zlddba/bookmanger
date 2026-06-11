package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.BorrowRecord
import org.zl.team.mapper.BookMapper
import org.zl.team.mapper.BorrowRecordMapper
import org.zl.team.mapper.MemberMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object BorrowService {

    /** 最大同时借阅数量 */
    const val MAX_BORROW = 3
    /** 借阅天数 */
    private const val BORROW_DAYS = 30L
    /** 最大续借次数 */
    private const val MAX_RENEW = 1

    fun listAll(): List<BorrowRecord> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BorrowRecordMapper::class.java).selectAll()
        } finally { session.close() }
    }

    fun getById(id: Int): BorrowRecord? {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BorrowRecordMapper::class.java).selectById(id)
        } finally { session.close() }
    }

    fun listByMember(cardNo: String): List<BorrowRecord> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BorrowRecordMapper::class.java).selectByMember(cardNo)
        } finally { session.close() }
    }

    fun listByStatus(status: String): List<BorrowRecord> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BorrowRecordMapper::class.java).selectByStatus(status)
        } finally { session.close() }
    }

    fun getActiveBorrowCount(cardNo: String): Int {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BorrowRecordMapper::class.java).selectActiveByMember(cardNo).size
        } finally { session.close() }
    }

    fun borrow(bookId: String, cardNo: String, date: String, dueDate: String? = null): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val bookMapper = session.getMapper(BookMapper::class.java)
            val memberMapper = session.getMapper(MemberMapper::class.java)
            val borrowMapper = session.getMapper(BorrowRecordMapper::class.java)

            val book = bookMapper.selectById(bookId) ?: return false
            if (book.stock <= 0) return false

            if (memberMapper.selectById(cardNo) == null) return false

            val active = borrowMapper.selectActiveByMember(cardNo)
            if (active.size >= MAX_BORROW) return false

            val finalDueDate = dueDate ?: LocalDate.parse(date)
                .plusDays(BORROW_DAYS)
                .format(DateTimeFormatter.ISO_LOCAL_DATE)

            borrowMapper.insert(BorrowRecord(
                bookId = bookId,
                cardNo = cardNo,
                borrowDate = date,
                dueDate = finalDueDate,
                remark = null
            ))

            bookMapper.updateStock(bookId, book.stock - 1)
            session.commit()
            OperationLogService.log("借阅", "图书", bookId, "借书: $bookId -> $cardNo, 应还: $finalDueDate")
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally { session.close() }
    }

    fun returnBook(id: Int, returnDate: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val borrowMapper = session.getMapper(BorrowRecordMapper::class.java)
            val bookMapper = session.getMapper(BookMapper::class.java)

            val record = borrowMapper.selectById(id) ?: return false
            if (record.status == "已归还") return false

            borrowMapper.updateReturn(id, returnDate, "已归还")
            val book = bookMapper.selectById(record.bookId)
            if (book != null) {
                bookMapper.updateStock(record.bookId, book.stock + 1)
            }

            session.commit()
            OperationLogService.log("归还", "图书", record.bookId, "还书: id=$id, 日期: $returnDate")
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally { session.close() }
    }

    fun getOverdueCount(): Int {
        val session = MyBatisUtil.getSqlSession()
        try {
            val all = session.getMapper(BorrowRecordMapper::class.java).selectAll()
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            return all.count { it.status != "已归还" && it.dueDate < today }
        } finally { session.close() }
    }

    fun renew(id: Int): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val borrowMapper = session.getMapper(BorrowRecordMapper::class.java)

            val record = borrowMapper.selectById(id) ?: return false
            if (record.status == "已归还") return false
            if (record.renewCount >= MAX_RENEW) return false

            val newDueDate = LocalDate.parse(record.dueDate)
                .plusDays(BORROW_DAYS)
                .format(DateTimeFormatter.ISO_LOCAL_DATE)

            borrowMapper.updateRenew(id, newDueDate, record.renewCount + 1)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally { session.close() }
    }
}
