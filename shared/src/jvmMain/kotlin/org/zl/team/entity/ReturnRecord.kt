package org.zl.team.entity

data class ReturnRecord(
    val id: Int = 0,         // ID（自增）
    val supplierId: String?, // 供应商编号
    val bookId: String?,     // 图书编号
    val unitPrice: Double?,  // 进价
    val quantity: Int?,      // 退货数量
    val amount: Double?,     // 金额
    val reason: String?,     // 退货原因
    val date: String?,       // 退货日期
    val remark: String?      // 备注
)
