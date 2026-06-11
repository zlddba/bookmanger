package org.zl.team.mapper

import org.zl.team.entity.OperationLog

interface OperationLogMapper {
    fun selectAll(): List<OperationLog>
    fun selectRecent(limit: Int): List<OperationLog>
    fun selectByTarget(target: String): List<OperationLog>
    fun insert(log: OperationLog): Int
}
