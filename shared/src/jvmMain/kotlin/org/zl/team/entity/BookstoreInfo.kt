package org.zl.team.entity

data class BookstoreInfo(
    val name: String,        // 书店名称
    val address: String?,    // 地址
    val website: String?,    // 网址
    val contact: String?,    // 联系人
    val phone: String?,      // 电话
    val mobile: String?,     // 手机
    val email: String?,      // 电子邮件
    val description: String?,// 书店简介
    val remark: String?      // 备注
)
