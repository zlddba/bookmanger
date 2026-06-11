package org.zl.team.mapper

import org.zl.team.entity.Feedback

interface FeedbackMapper {
    fun selectById(id: Int): Feedback?
    fun selectAll(): List<Feedback>
    fun insert(feedback: Feedback): Int
    fun delete(id: Int): Int
}
