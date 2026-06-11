package org.zl.team.mapper

import org.zl.team.entity.BookstoreInfo

interface BookstoreInfoMapper {
    fun selectByName(name: String): BookstoreInfo?
    fun selectAll(): List<BookstoreInfo>
    fun insert(info: BookstoreInfo): Int
    fun update(info: BookstoreInfo): Int
    fun delete(name: String): Int
}
