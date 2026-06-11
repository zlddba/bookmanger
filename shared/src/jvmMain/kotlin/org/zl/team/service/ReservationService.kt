package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.BookReservation
import org.zl.team.mapper.BookReservationMapper

object ReservationService {

    fun listAll(): List<BookReservation> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BookReservationMapper::class.java).selectAll()
        } finally {
            session.close()
        }
    }

    fun listByStatus(status: String): List<BookReservation> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BookReservationMapper::class.java).selectByStatus(status)
        } finally {
            session.close()
        }
    }

    fun create(reservation: BookReservation): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            session.getMapper(BookReservationMapper::class.java).insert(reservation)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    fun markCompleted(id: Int): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val now = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            session.getMapper(BookReservationMapper::class.java).updateStatus(id, "已到货", now)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    fun cancel(id: Int): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val now = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            session.getMapper(BookReservationMapper::class.java).updateStatus(id, "已取消", now)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }
}
