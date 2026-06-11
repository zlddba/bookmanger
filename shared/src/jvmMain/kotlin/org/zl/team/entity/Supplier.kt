package org.zl.team.entity

data class Supplier(
    val supplierId: String,  // 供应商编号
    val name: String,        // 供应商名称
    val address: String?,    // 地址
    val website: String?,    // 网址
    val contact: String?,    // 联系人
    val phone: String?,      // 电话
    val fax: String?,        // 传真
    val email: String?,      // 电子邮件
    val description: String? // 单位简介
)
