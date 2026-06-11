package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.service.EmployeeService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeServiceTest {

    private val testDbPath = "test_employee.db"

    @BeforeAll
    fun setUp() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
        DatabaseInitializer.initForTest(testDbPath)
    }

    @AfterAll
    fun tearDown() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
    }

    @Test
    fun `createEmployee should succeed with valid data`() {
        val result = EmployeeService.createEmployee(
            "testuser", "测试员工", "123456",
            "男", "测试地址", "010-12345678",
            "13800138000", "test@example.com", "格言测试", "售书员"
        )
        assertTrue(result)

        // 验证创建后可通过登录验证
        val employees = EmployeeService.listAllEmployees()
        val created = employees.find { it.first.userId == "testuser" }
        assertNotNull(created)
        assertEquals("测试员工", created!!.second?.name)
        assertEquals("售书员", created.first.role)
    }

    @Test
    fun `createEmployee should fail with duplicate account`() {
        // 用不同角色创建同名帐号
        val result = EmployeeService.createEmployee(
            "testuser2", "测试", "123456",
            null, null, null, null, null, null, "仓库管理员"
        )
        assertTrue(result)

        // 再次创建同一帐号应失败
        val dupResult = EmployeeService.createEmployee(
            "testuser2", "重复", "123456",
            null, null, null, null, null, null, "售书员"
        )
        assertFalse(dupResult)
    }

    @Test
    fun `listAllEmployees should include initial admin accounts`() {
        val employees = EmployeeService.listAllEmployees()
        val userIds = employees.map { it.first.userId }
        assertTrue(userIds.contains("admin"))
        assertTrue(userIds.contains("仓库管理员"))
        assertTrue(userIds.contains("售书员"))
    }

    @Test
    fun `deleteEmployee should remove both admin and employee records`() {
        // 先创建
        EmployeeService.createEmployee(
            "todel", "待删除", "123456",
            null, null, null, null, null, null, "会员"
        )

        // 确认存在
        var list = EmployeeService.listAllEmployees()
        assertNotNull(list.find { it.first.userId == "todel" })

        // 删除
        val result = EmployeeService.deleteEmployee("todel")
        assertTrue(result)

        // 确认已删除
        list = EmployeeService.listAllEmployees()
        assertNull(list.find { it.first.userId == "todel" })
    }

    @Test
    fun `changePassword should succeed with correct old password`() {
        EmployeeService.createEmployee(
            "chgpwd", "密码测试", "oldpass",
            null, null, null, null, null, null, "售书员"
        )

        val result = EmployeeService.changePassword("chgpwd", "oldpass", "newpass")
        assertTrue(result)
    }

    @Test
    fun `changePassword should fail with wrong old password`() {
        EmployeeService.createEmployee(
            "chgpwd2", "密码测试2", "oldpass",
            null, null, null, null, null, null, "售书员"
        )

        val result = EmployeeService.changePassword("chgpwd2", "wrongpass", "newpass")
        assertFalse(result)
    }

    @Test
    fun `changePassword should return false for nonexistent user`() {
        val result = EmployeeService.changePassword("nobody", "old", "new")
        assertFalse(result)
    }

    @Test
    fun `resetPassword should force change password`() {
        EmployeeService.createEmployee(
            "resetpwd", "重置测试", "oldpass",
            null, null, null, null, null, null, "售书员"
        )

        val result = EmployeeService.resetPassword("resetpwd", "newforced")
        assertTrue(result)
    }

    @Test
    fun `resetPassword should return false for nonexistent user`() {
        val result = EmployeeService.resetPassword("nobody", "new")
        assertFalse(result)
    }

    @Test
    fun `updateEmployee should update all editable fields`() {
        EmployeeService.createEmployee(
            "edituser", "原始名", "123456",
            "男", "原始地址", "010-11111111",
            "13800000001", "old@test.com", "原始格言", "售书员"
        )

        val result = EmployeeService.updateEmployee(
            "edituser", "新名字", "女",
            "新地址", "010-22222222", "13800000002",
            "new@test.com", "新格言"
        )
        assertTrue(result)

        // 验证更新结果
        val list = EmployeeService.listAllEmployees()
        val updated = list.find { it.first.userId == "edituser" }
        assertNotNull(updated)
        assertEquals("新名字", updated!!.second?.name)
        assertEquals("女", updated.second?.gender)
        assertEquals("新地址", updated.second?.address)
        assertEquals("010-22222222", updated.second?.phone)
        assertEquals("13800000002", updated.second?.mobile)
        assertEquals("new@test.com", updated.second?.email)
        assertEquals("新格言", updated.second?.motto)
    }

    @Test
    fun `updateEmployee should return false for nonexistent user`() {
        val result = EmployeeService.updateEmployee(
            "nobody", "名", null, null, null, null, null, null
        )
        assertFalse(result)
    }

    @Test
    fun `deleteEmployee should refuse to delete admin`() {
        val result = EmployeeService.deleteEmployee("admin")
        assertFalse(result)

        // 确认 admin 仍然存在
        val list = EmployeeService.listAllEmployees()
        assertNotNull(list.find { it.first.userId == "admin" })
    }
}
