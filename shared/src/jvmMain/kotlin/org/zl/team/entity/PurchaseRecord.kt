package org.zl.team.entity

data class PurchaseRecord(
    val id: Int = 0,         // ID（自增）
    val supplierId: String?, // 供应商编号
    val bookId: String?,     // 图书编号
    val quantity: Int?,      // 数量
    val unitPrice: Double?,  // 单价（进价）
    val discount: Double = 1.0, // 折扣
    val amount: Double?,     // 金额 = 数量*单价*折扣
    val date: String?,       // 进书日期
    val remark: String?      // 备注
)
