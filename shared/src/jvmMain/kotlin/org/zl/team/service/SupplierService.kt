package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.Supplier
import org.zl.team.mapper.SupplierMapper

object SupplierService {

    fun listAll(): List<Supplier> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(SupplierMapper::class.java).selectAll()
        } finally { session.close() }
    }

    fun getById(id: String): Supplier? {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(SupplierMapper::class.java).selectById(id)
        } finally { session.close() }
    }

    fun create(supplier: Supplier): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(SupplierMapper::class.java)
            if (mapper.selectById(supplier.supplierId) != null) return false
            mapper.insert(supplier)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback(); throw e
        } finally { session.close() }
    }

    fun update(supplier: Supplier): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(SupplierMapper::class.java)
            if (mapper.selectById(supplier.supplierId) == null) return false
            mapper.update(supplier)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback(); throw e
        } finally { session.close() }
    }

    fun delete(id: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(SupplierMapper::class.java)
            if (mapper.selectById(id) == null) return false
            mapper.delete(id)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback(); throw e
        } finally { session.close() }
    }
}
