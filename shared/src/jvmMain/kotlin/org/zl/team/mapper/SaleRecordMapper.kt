package org.zl.team.mapper

import org.zl.team.entity.SaleRecord
import org.apache.ibatis.annotations.Param

interface SaleRecordMapper {
    fun selectById(id: Int): SaleRecord?
    fun selectAll(): List<SaleRecord>
    fun selectByDate(date: String): List<SaleRecord>
    fun selectByDateRange(@Param("startDate") startDate: String, @Param("endDate") endDate: String): List<SaleRecord>
    fun selectByMember(cardNo: String): List<SaleRecord>
    fun insert(record: SaleRecord): Int
    fun delete(id: Int): Int
}
