package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.Book
import org.zl.team.mapper.BookMapper

object BookService {

    fun listAll(): List<Book> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BookMapper::class.java).selectAll()
        } finally {
            session.close()
        }
    }

    fun search(keyword: String): List<Book> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BookMapper::class.java).searchByKeyword(keyword)
        } finally {
            session.close()
        }
    }

    fun getById(bookId: String): Book? {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(BookMapper::class.java).selectById(bookId)
        } finally {
            session.close()
        }
    }

    fun create(book: Book): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(BookMapper::class.java)
            if (mapper.selectById(book.bookId) != null) return false
            mapper.insert(book)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    fun update(book: Book): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(BookMapper::class.java)
            if (mapper.selectById(book.bookId) == null) return false
            mapper.update(book)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    fun delete(bookId: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(BookMapper::class.java)
            if (mapper.selectById(bookId) == null) return false
            mapper.delete(bookId)
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
