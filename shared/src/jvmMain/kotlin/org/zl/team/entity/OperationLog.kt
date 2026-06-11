package org.zl.team.entity

data class OperationLog(
    val id: Int = 0,
    val action: String,
    val target: String,
    val targetId: String? = null,
    val detail: String? = null,
    val operator: String,
    val createdAt: String? = null
)
