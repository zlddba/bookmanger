package org.zl.team.entity

data class Employee(
    val account: String,    // 员工帐号
    val name: String,       // 姓名
    val gender: String?,    // 性别
    val address: String?,   // 地址
    val phone: String?,     // 电话
    val mobile: String?,    // 手机
    val email: String?,     // 电子邮件
    val motto: String?,     // 人生格言
    val createdAt: String?  // 创建日期
)
