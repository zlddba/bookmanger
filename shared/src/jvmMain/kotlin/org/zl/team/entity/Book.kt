package org.zl.team.entity

data class Book(
    val bookId: String,      // 图书编号
    val categoryId: String?, // 图书分类号
    val title: String,       // 书名
    val series: String?,     // 丛书
    val author: String?,     // 作者
    val publisher: String?,  // 出版社
    val edition: String?,    // 版次
    val isbn: String?,       // ISBN
    val price: Double?,      // 定价
    val stock: Int = 0,      // 库存量
    val description: String?,// 内容简介
    val keywords: String?,   // 关键词
    val publishDate: String?,// 出版日期
    val createdAt: String?   // 入库时间
)
