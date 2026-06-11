package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.mapper.AdminMapper
import org.zl.team.util.PasswordUtil

object LoginService {

    fun verify(userId: String, password: String): String? {
        if (userId.isBlank() || password.isBlank()) return null

        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(AdminMapper::class.java)
            val admin = mapper.selectById(userId) ?: return null

            if (PasswordUtil.verify(password, admin.password)) {
                // 自动升级明文密码为哈希
                if (!admin.password.contains(":")) {
                    mapper.update(admin.copy(password = PasswordUtil.hash(password)))
                    session.commit()
                }
                return admin.role
            }
        } finally {
            session.close()
        }
        return null
    }
}
