package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.Admin
import org.zl.team.entity.Member
import org.zl.team.mapper.AdminMapper
import org.zl.team.mapper.MemberMapper
import org.zl.team.mapper.MemberPolicyMapper
import org.zl.team.util.PasswordUtil

object MemberService {

    fun listAll(): List<Member> {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(MemberMapper::class.java).selectAll() }
        finally { session.close() }
    }

    fun getById(cardNo: String): Member? {
        val session = MyBatisUtil.getSqlSession()
        try { return session.getMapper(MemberMapper::class.java).selectById(cardNo) }
        finally { session.close() }
    }

    fun getDiscount(cardNo: String): Double {
        val session = MyBatisUtil.getSqlSession()
        try {
            val member = session.getMapper(MemberMapper::class.java).selectById(cardNo) ?: return 1.0
            val policy = session.getMapper(MemberPolicyMapper::class.java).selectByLevel(member.level)
            return policy?.discount?.toDoubleOrNull() ?: 1.0
        } finally { session.close() }
    }

    fun create(member: Member, password: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val memberMapper = session.getMapper(MemberMapper::class.java)
            val adminMapper = session.getMapper(AdminMapper::class.java)
            if (memberMapper.selectById(member.cardNo) != null) return false
            if (adminMapper.selectById(member.cardNo) != null) return false
            memberMapper.insert(member)
            adminMapper.insert(Admin(member.cardNo, PasswordUtil.hash(password), "会员"))
            session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }

    fun update(member: Member): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(MemberMapper::class.java)
            if (mapper.selectById(member.cardNo) == null) return false
            mapper.update(member); session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }

    fun delete(cardNo: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(MemberMapper::class.java)
            val adminMapper = session.getMapper(AdminMapper::class.java)
            if (mapper.selectById(cardNo) == null) return false
            mapper.delete(cardNo)
            adminMapper.delete(cardNo)
            session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }

    fun changePassword(cardNo: String, oldPassword: String, newPassword: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val adminMapper = session.getMapper(AdminMapper::class.java)
            val admin = adminMapper.selectById(cardNo) ?: return false
            if (!PasswordUtil.verify(oldPassword, admin.password)) return false
            adminMapper.update(admin.copy(password = PasswordUtil.hash(newPassword)))
            session.commit()
            return true
        } catch (e: Exception) { session.rollback(); throw e }
        finally { session.close() }
    }
}
