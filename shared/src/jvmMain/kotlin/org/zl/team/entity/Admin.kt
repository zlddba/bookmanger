package org.zl.team.entity

data class Admin(
    val userId: String,     // 用户ID
    val password: String,   // 用户密码
    val role: String        // 用户身份：经理/仓库管理员/售书员/会员/游客
)
