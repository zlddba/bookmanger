package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.MemberPolicy
import org.zl.team.mapper.MemberPolicyMapper

object MemberPolicyService {

    fun listAll(): List<MemberPolicy> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(MemberPolicyMapper::class.java).selectAll() }
        finally { session.close() }
    }

    fun getByLevel(level: Int): MemberPolicy? {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(MemberPolicyMapper::class.java).selectByLevel(level) }
        finally { session.close() }
    }

    fun create(policy: MemberPolicy): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(MemberPolicyMapper::class.java)
            if (mapper.selectByLevel(policy.level) != null) return false
            mapper.insert(policy); session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }

    fun update(policy: MemberPolicy): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(MemberPolicyMapper::class.java)
            if (mapper.selectByLevel(policy.level) == null) return false
            mapper.update(policy); session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }

    fun delete(level: Int): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(MemberPolicyMapper::class.java)
            if (mapper.selectByLevel(level) == null) return false
            mapper.delete(level); session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }
}
