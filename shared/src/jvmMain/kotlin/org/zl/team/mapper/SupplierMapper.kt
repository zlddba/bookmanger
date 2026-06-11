package org.zl.team.mapper

import org.zl.team.entity.Supplier

interface SupplierMapper {
    fun selectById(supplierId: String): Supplier?
    fun selectAll(): List<Supplier>
    fun insert(supplier: Supplier): Int
    fun update(supplier: Supplier): Int
    fun delete(supplierId: String): Int
}
