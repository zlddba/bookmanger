package org.zl.team.entity

data class MemberPolicy(
    val level: Int,          // 会员级别（1-5）
    val minAmount: Int?,     // 会员标准：达到该金额可升级
    val discount: String = "1.0", // 打折（如 0.9）
    val gift: String?,       // 赠送礼品
    val remark: String?      // 备注
)
