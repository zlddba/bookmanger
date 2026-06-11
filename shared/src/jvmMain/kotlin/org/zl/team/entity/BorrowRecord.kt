package org.zl.team.entity

data class BorrowRecord(
    val id: Int = 0,
    val bookId: String,
    val cardNo: String,
    val borrowDate: String,
    val dueDate: String,
    val returnDate: String? = null,
    val status: String = "借阅中",
    val renewCount: Int = 0,
    val remark: String? = null
)
