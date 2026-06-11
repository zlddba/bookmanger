package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.Feedback
import org.zl.team.mapper.FeedbackMapper

object FeedbackService {
    fun listAll(): List<Feedback> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(FeedbackMapper::class.java).selectAll() }
        finally { session.close() }
    }

    fun submit(feedback: Feedback): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            session.getMapper(FeedbackMapper::class.java).insert(feedback)
            session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }
}
