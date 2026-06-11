package org.zl.team.mapper

import org.zl.team.entity.ReturnRecord
import org.apache.ibatis.annotations.Param

interface ReturnRecordMapper {
    fun selectById(id: Int): ReturnRecord?
    fun selectAll(): List<ReturnRecord>
    fun selectBySupplier(supplierId: String): List<ReturnRecord>
    fun selectByDateRange(@Param("startDate") startDate: String, @Param("endDate") endDate: String): List<ReturnRecord>
    fun insert(record: ReturnRecord): Int
    fun delete(id: Int): Int
}
