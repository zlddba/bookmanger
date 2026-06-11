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
            OperationLogService.log("新增", "图书", book.bookId, "新增图书: ${book.title}")
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
            OperationLogService.log("修改", "图书", book.bookId, "修改图书: ${book.title}")
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
            val book = mapper.selectById(bookId) ?: return false
            mapper.delete(bookId)
            session.commit()
            OperationLogService.log("删除", "图书", bookId, "删除图书: ${book.title}")
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }
}
