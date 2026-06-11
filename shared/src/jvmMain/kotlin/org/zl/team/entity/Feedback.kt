package org.zl.team.entity

data class Feedback(
    val id: Int = 0,         // ID（自增）
    val name: String?,       // 姓名
    val role: String?,       // 身份
    val gender: String?,     // 性别
    val company: String?,    // 单位
    val address: String?,    // 地址
    val email: String?,      // 电子邮件
    val content: String?,    // 反馈信息
    val date: String?        // 反馈日期
)
