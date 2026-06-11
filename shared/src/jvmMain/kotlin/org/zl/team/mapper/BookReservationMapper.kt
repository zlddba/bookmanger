package org.zl.team.mapper

import org.zl.team.entity.BookReservation
import org.apache.ibatis.annotations.Param

interface BookReservationMapper {
    fun selectById(id: Int): BookReservation?
    fun selectAll(): List<BookReservation>
    fun selectByStatus(status: String): List<BookReservation>
    fun insert(reservation: BookReservation): Int
    fun updateStatus(@Param("id") id: Int, @Param("status") status: String, @Param("resolvedAt") resolvedAt: String?): Int
    fun delete(id: Int): Int
}
