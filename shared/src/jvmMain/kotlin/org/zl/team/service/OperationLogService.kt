package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.OperationLog
import org.zl.team.mapper.OperationLogMapper
import org.zl.team.util.SessionManager

object OperationLogService {

    fun listAll(): List<OperationLog> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(OperationLogMapper::class.java).selectAll()
        } finally {
            session.close()
        }
    }

    fun listRecent(limit: Int = 50): List<OperationLog> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(OperationLogMapper::class.java).selectRecent(limit)
        } finally {
            session.close()
        }
    }

    fun listByTarget(target: String): List<OperationLog> {
        val session = MyBatisUtil.getSqlSession()
        try {
            return session.getMapper(OperationLogMapper::class.java).selectByTarget(target)
        } finally {
            session.close()
        }
    }

    fun log(action: String, target: String, targetId: String? = null, detail: String? = null) {
        val operator = try { SessionManager.currentUserName } catch (e: Exception) { "系统" }
        val session = MyBatisUtil.getSqlSession()
        try {
            session.getMapper(OperationLogMapper::class.java).insert(
                OperationLog(action = action, target = target, targetId = targetId, detail = detail, operator = operator)
            )
            session.commit()
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }
}
