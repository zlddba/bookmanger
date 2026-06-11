package org.zl.team.entity

data class BookPrice(
    val bookId: String,   // 图书编号
    val price: Double?,   // 进价（最近进价）
    val date: String?     // 进书日期
)
