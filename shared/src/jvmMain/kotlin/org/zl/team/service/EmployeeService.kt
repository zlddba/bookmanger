package org.zl.team.service

import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.Admin
import org.zl.team.entity.Employee
import org.zl.team.mapper.AdminMapper
import org.zl.team.mapper.EmployeeMapper
import org.zl.team.util.PasswordUtil

object EmployeeService {

    /**
     * 创建员工（事务：Admin 表 + 员工表）
     */
    fun createEmployee(account: String, name: String, password: String,
                       gender: String?, address: String?, phone: String?,
                       mobile: String?, email: String?, motto: String?,
                       role: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val adminMapper = session.getMapper(AdminMapper::class.java)
            val empMapper = session.getMapper(EmployeeMapper::class.java)

            if (adminMapper.selectById(account) != null) {
                return false // 帐号已存在
            }

            val admin = Admin(account, PasswordUtil.hash(password), role)
            adminMapper.insert(admin)

            val emp = Employee(account, name, gender, address, phone, mobile, email, motto, null)
            empMapper.insert(emp)

            session.commit()
            OperationLogService.log("新增", "员工", account, "新增员工: $name ($role)")
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    /**
     * 更新员工信息（Employee 表字段）
     */
    fun updateEmployee(account: String, name: String, gender: String?,
                       address: String?, phone: String?, mobile: String?,
                       email: String?, motto: String?): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val empMapper = session.getMapper(EmployeeMapper::class.java)
            val existing = empMapper.selectById(account) ?: return false

            empMapper.update(existing.copy(
                name = name, gender = gender, address = address,
                phone = phone, mobile = mobile, email = email, motto = motto
            ))
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    /**
     * 保存员工信息：不存在则插入，存在则更新
     */
    fun saveEmployee(account: String, name: String, gender: String?,
                     address: String?, phone: String?, mobile: String?,
                     email: String?, motto: String?): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val empMapper = session.getMapper(EmployeeMapper::class.java)
            val existing = empMapper.selectById(account)

            if (existing != null) {
                empMapper.update(existing.copy(
                    name = name, gender = gender, address = address,
                    phone = phone, mobile = mobile, email = email, motto = motto
                ))
            } else {
                val emp = Employee(account, name, gender, address, phone, mobile, email, motto, null)
                empMapper.insert(emp)
            }
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    /**
     * 删除员工（事务：Admin 表 + 员工表）
     * 内置管理员 admin 不可删除
     */
    fun deleteEmployee(account: String): Boolean {
        if (account == "admin") return false
        val session = MyBatisUtil.getSqlSession()
        try {
            val adminMapper = session.getMapper(AdminMapper::class.java)
            val empMapper = session.getMapper(EmployeeMapper::class.java)

            val emp = empMapper.selectById(account)
            adminMapper.delete(account)
            empMapper.delete(account)

            session.commit()
            OperationLogService.log("删除", "员工", account, "删除员工: ${emp?.name ?: account}")
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    /**
     * 获取所有员工（含 Admin.role 信息）
     */
    fun listAllEmployees(): List<Pair<Admin, Employee?>> {
        val session = MyBatisUtil.getSqlSession()
        try {
            val adminMapper = session.getMapper(AdminMapper::class.java)
            val empMapper = session.getMapper(EmployeeMapper::class.java)

            val admins = adminMapper.selectAll()
            return admins.map { admin ->
                admin to empMapper.selectById(admin.userId)
            }
        } finally {
            session.close()
        }
    }

    /**
     * 修改密码
     */
    fun changePassword(userId: String, oldPassword: String, newPassword: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(AdminMapper::class.java)
            val admin = mapper.selectById(userId) ?: return false

            if (!PasswordUtil.verify(oldPassword, admin.password)) {
                return false
            }

            mapper.update(admin.copy(password = PasswordUtil.hash(newPassword)))
            session.commit()
            return true
        } catch (e: Exception) {
            session.rollback()
            throw e
        } finally {
            session.close()
        }
    }

    /**
     * 强制重置密码（经理权限）
     */
    fun resetPassword(userId: String, newPassword: String): Boolean {
        val session = MyBatisUtil.getSqlSession()
        try {
            val mapper = session.getMapper(AdminMapper::class.java)
            val admin = mapper.selectById(userId) ?: return false

            mapper.update(admin.copy(password = PasswordUtil.hash(newPassword)))
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
