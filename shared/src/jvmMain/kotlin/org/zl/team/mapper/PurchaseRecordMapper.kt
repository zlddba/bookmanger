package org.zl.team.mapper

import org.zl.team.entity.PurchaseRecord
import org.apache.ibatis.annotations.Param

interface PurchaseRecordMapper {
    fun selectById(id: Int): PurchaseRecord?
    fun selectAll(): List<PurchaseRecord>
    fun selectBySupplier(supplierId: String): List<PurchaseRecord>
    fun selectByDateRange(@Param("startDate") startDate: String, @Param("endDate") endDate: String): List<PurchaseRecord>
    fun insert(record: PurchaseRecord): Int
    fun delete(id: Int): Int
}
