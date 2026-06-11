package org.zl.team

import org.zl.team.util.PasswordUtil
import org.zl.team.util.SessionManager
import org.zl.team.util.ValidationUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class UtilTest {

    // ==================== SessionManager ====================

    @BeforeEach
    fun resetSession() {
        SessionManager.logout()
    }

    @Test
    fun `sessionManager initial state is logged out`() {
        assertFalse(SessionManager.isLoggedIn())
        assertEquals("", SessionManager.currentUserId)
        assertEquals("", SessionManager.currentUserRole)
    }

    @Test
    fun `sessionManager login and logout`() {
        SessionManager.login("admin", "经理")
        assertTrue(SessionManager.isLoggedIn())
        assertEquals("admin", SessionManager.currentUserId)
        assertEquals("经理", SessionManager.currentUserRole)

        SessionManager.logout()
        assertFalse(SessionManager.isLoggedIn())
        assertEquals("", SessionManager.currentUserId)
    }

    @Test
    fun `sessionManager hasRole checks`() {
        SessionManager.login("user1", "仓库管理员")
        assertTrue(SessionManager.hasRole("仓库管理员"))
        assertTrue(SessionManager.hasRole("经理", "仓库管理员"))
        assertFalse(SessionManager.hasRole("售书员"))
        assertFalse(SessionManager.hasRole("经理", "售书员"))
    }

    @Test
    fun `sessionManager hasRole returns false when logged out`() {
        assertFalse(SessionManager.hasRole("经理"))
    }

    // ==================== PasswordUtil ====================

    @Test
    fun `password hash and verify`() {
        val hash = PasswordUtil.hash("mypassword")
        assertTrue(hash.contains(":"))
        assertTrue(PasswordUtil.verify("mypassword", hash))
        assertFalse(PasswordUtil.verify("wrongpassword", hash))
    }

    @Test
    fun `password each hash is unique due to salt`() {
        val hash1 = PasswordUtil.hash("password")
        val hash2 = PasswordUtil.hash("password")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun `password verifySimple works with plain text`() {
        assertTrue(PasswordUtil.verifySimple("admin", "admin"))
        assertFalse(PasswordUtil.verifySimple("wrong", "admin"))
    }

    @Test
    fun `password verifySimple works with hashed`() {
        val hash = PasswordUtil.hash("secure123")
        assertTrue(PasswordUtil.verifySimple("secure123", hash))
        assertFalse(PasswordUtil.verifySimple("wrong", hash))
    }

    // ==================== ValidationUtil ====================

    @Test
    fun `validation isBlank and isNotBlank`() {
        assertTrue(ValidationUtil.isBlank(null))
        assertTrue(ValidationUtil.isBlank(""))
        assertTrue(ValidationUtil.isBlank("   "))
        assertTrue(ValidationUtil.isNotBlank("hello"))
        assertFalse(ValidationUtil.isNotBlank(null))
    }

    @Test
    fun `validation isValidEmail`() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"))
        assertTrue(ValidationUtil.isValidEmail("a.b@c.co"))
        assertFalse(ValidationUtil.isValidEmail(null))
        assertFalse(ValidationUtil.isValidEmail("notanemail"))
        assertFalse(ValidationUtil.isValidEmail("@nouser.com"))
    }

    @Test
    fun `validation isValidPhone`() {
        assertTrue(ValidationUtil.isValidPhone("010-12345678"))
        assertTrue(ValidationUtil.isValidPhone("13800138000"))
        assertFalse(ValidationUtil.isValidPhone(null))
        assertFalse(ValidationUtil.isValidPhone("123"))
    }

    @Test
    fun `validation isValidIsbn`() {
        assertTrue(ValidationUtil.isValidIsbn("978-7-302-32332-3"))  // ISBN-13
        assertTrue(ValidationUtil.isValidIsbn("730208889X"))          // ISBN-10
        assertFalse(ValidationUtil.isValidIsbn(null))
        assertFalse(ValidationUtil.isValidIsbn("abc"))
    }

    @Test
    fun `validation numeric checks`() {
        assertTrue(ValidationUtil.isPositiveInt("5"))
        assertFalse(ValidationUtil.isPositiveInt("0"))
        assertFalse(ValidationUtil.isPositiveInt("-1"))
        assertFalse(ValidationUtil.isPositiveInt("abc"))

        assertTrue(ValidationUtil.isNonNegativeInt("0"))
        assertFalse(ValidationUtil.isNonNegativeInt("-5"))

        assertTrue(ValidationUtil.isPositiveDouble("3.14"))
        assertFalse(ValidationUtil.isPositiveDouble("0.0"))

        assertTrue(ValidationUtil.isNonNegativeDouble("0.0"))
        assertFalse(ValidationUtil.isNonNegativeDouble("-0.1"))
    }
}
