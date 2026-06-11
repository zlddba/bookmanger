package org.zl.team.mapper

import org.zl.team.entity.Book
import org.apache.ibatis.annotations.Param

interface BookMapper {
    fun selectById(bookId: String): Book?
    fun selectAll(): List<Book>
    fun searchByKeyword(keyword: String): List<Book>
    fun insert(book: Book): Int
    fun update(book: Book): Int
    fun updateStock(@Param("bookId") bookId: String, @Param("stock") stock: Int): Int
    fun delete(bookId: String): Int
}
