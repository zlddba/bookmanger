package org.zl.team.entity

data class BookCategory(
    val categoryId: String,  // 图书分类号
    val name: String,        // 图书分类
    val parentId: String?    // 所属父类编号（顶级为空）
)
