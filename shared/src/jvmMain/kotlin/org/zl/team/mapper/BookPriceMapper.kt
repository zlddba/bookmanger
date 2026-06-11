package org.zl.team.mapper

import org.zl.team.entity.BookPrice

interface BookPriceMapper {
    fun selectById(bookId: String): BookPrice?
    fun insert(bookPrice: BookPrice): Int
    fun update(bookPrice: BookPrice): Int
    fun delete(bookId: String): Int
}
