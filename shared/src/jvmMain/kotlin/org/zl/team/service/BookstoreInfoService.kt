package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.BookstoreInfo
import org.zl.team.mapper.BookstoreInfoMapper

object BookstoreInfoService {

    fun getInfo(): BookstoreInfo? {
        val session = MyBatisUtil.getSqlSession()
        try {
            val list = session.getMapper(BookstoreInfoMapper::class.java).selectAll()
            return list.firstOrNull()
        } finally {
            session.close()
        }
    }

    fun update(info: BookstoreInfo): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            session.getMapper(BookstoreInfoMapper::class.java).update(info)
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }
}
