package org.zl.team.entity

data class BookReservation(
    val id: Int = 0,
    val bookTitle: String,
    val author: String? = null,
    val publisher: String? = null,
    val isbn: String? = null,
    val customerName: String,
    val customerPhone: String? = null,
    val status: String = "待处理",
    val note: String? = null,
    val createdAt: String? = null,
    val resolvedAt: String? = null
)
