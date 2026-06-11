package org.zl.team.entity

data class Member(
    val cardNo: String,      // 会员卡号
    val level: Int = 1,       // 会员等级（1-5）
    val name: String,        // 姓名
    val gender: String?,     // 性别
    val address: String?,    // 地址
    val company: String?,    // 单位
    val phone: String?,      // 电话
    val email: String?,      // 电子邮件
    val motto: String?,      // 人生格言
    val regDate: String?,    // 注册日期
    val totalSpent: Double = 0.0  // 累计消费额
)
