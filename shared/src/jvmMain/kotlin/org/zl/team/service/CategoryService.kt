package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.BookCategory
import org.zl.team.mapper.BookCategoryMapper

object CategoryService {

    fun listAll(): List<BookCategory> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(BookCategoryMapper::class.java).selectAll() }
        finally { session.close() }
    }

    fun getTopLevel(): List<BookCategory> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(BookCategoryMapper::class.java).selectByParentId(null) }
        finally { session.close() }
    }

    fun getChildren(parentId: String): List<BookCategory> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(BookCategoryMapper::class.java).selectByParentId(parentId) }
        finally { session.close() }
    }

    fun create(cat: BookCategory): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(BookCategoryMapper::class.java)
            if (mapper.selectById(cat.categoryId) != null) return false
            mapper.insert(cat); session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }

    fun update(cat: BookCategory): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(BookCategoryMapper::class.java)
            if (mapper.selectById(cat.categoryId) == null) return false
            mapper.update(cat); session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }

    fun delete(id: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(BookCategoryMapper::class.java)
            // 删除该分类下的子分类
            mapper.selectByParentId(id).forEach { mapper.delete(it.categoryId) }
            mapper.delete(id); session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }
}
