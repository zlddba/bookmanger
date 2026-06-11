package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.service.LoginService
import org.zl.team.util.SessionManager
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginServiceTest {

    private val testDbPath = "test_login.db"

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
    fun `login with correct admin credentials returns manager role`() {
        val role = LoginService.verify("admin", "admin")
        assertEquals("经理", role)
    }

    @Test
    fun `login with correct warehouse admin credentials`() {
        val role = LoginService.verify("仓库管理员", "admin")
        assertEquals("仓库管理员", role)
    }

    @Test
    fun `login with correct seller credentials`() {
        val role = LoginService.verify("售书员", "admin")
        assertEquals("售书员", role)
    }

    @Test
    fun `login with wrong password returns null`() {
        val role = LoginService.verify("admin", "wrong")
        assertNull(role)
    }

    @Test
    fun `login with nonexistent user returns null`() {
        val role = LoginService.verify("nonexistent", "admin")
        assertNull(role)
    }

    @Test
    fun `login with blank credentials returns null`() {
        assertNull(LoginService.verify("", ""))
        assertNull(LoginService.verify("admin", ""))
        assertNull(LoginService.verify("", "admin"))
    }
}
