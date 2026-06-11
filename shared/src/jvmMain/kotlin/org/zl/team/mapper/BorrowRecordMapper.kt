package org.zl.team.mapper

import org.zl.team.entity.BorrowRecord
import org.apache.ibatis.annotations.Param

interface BorrowRecordMapper {
    fun selectAll(): List<BorrowRecord>
    fun selectById(id: Int): BorrowRecord?
    fun selectByMember(cardNo: String): List<BorrowRecord>
    fun selectByBook(bookId: String): List<BorrowRecord>
    fun selectByStatus(status: String): List<BorrowRecord>
    fun selectActiveByMember(cardNo: String): List<BorrowRecord>
    fun insert(record: BorrowRecord): Int
    fun updateReturn(@Param("id") id: Int, @Param("returnDate") returnDate: String, @Param("status") status: String): Int
    fun updateRenew(@Param("id") id: Int, @Param("dueDate") dueDate: String, @Param("renewCount") renewCount: Int): Int
    fun delete(id: Int): Int
}
