package org.zl.team.mapper

import org.zl.team.entity.BookCategory

interface BookCategoryMapper {
    fun selectById(categoryId: String): BookCategory?
    fun selectAll(): List<BookCategory>
    fun selectByParentId(parentId: String?): List<BookCategory>
    fun insert(category: BookCategory): Int
    fun update(category: BookCategory): Int
    fun delete(categoryId: String): Int
}
