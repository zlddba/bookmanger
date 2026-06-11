package org.zl.team.entity

data class SaleRecord(
    val id: Int = 0,         // ID（自增）
    val bookId: String?,     // 图书编号
    val quantity: Int?,      // 数量
    val cardNo: String?,     // 会员卡号（可为空）
    val discount: Double = 1.0, // 实际打折
    val amount: Double?,     // 实收金额
    val date: String?,       // 日期
    val remark: String?      // 备注
)
