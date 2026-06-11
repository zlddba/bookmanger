package org.zl.team.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordUtil {

    private const val SALT_LENGTH = 16
    private const val HASH_ALGORITHM = "SHA-256"
    private const val ITERATIONS = 10000

    /**
     * 生成盐值 + 哈希后的密码，格式: "salt:hash"
     */
    fun hash(password: String): String {
        val salt = generateSalt()
        val hash = computeHash(password, salt)
        return "$salt:$hash"
    }

    /**
     * 验证密码是否匹配
     */
    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split(":", limit = 2)
        if (parts.size != 2) {
            // 兼容明文密码（初始数据中的 admin/admin 等）
            return password == stored
        }
        val salt = parts[0]
        val hash = parts[1]
        return computeHash(password, salt) == hash
    }

    /**
     * 简单验证（用于测试、兼容明文密码场景）
     */
    fun verifySimple(password: String, stored: String): Boolean {
        if (stored.contains(":")) {
            return verify(password, stored)
        }
        return password == stored
    }

    private fun generateSalt(): String {
        val bytes = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    private fun computeHash(password: String, salt: String): String {
        val md = MessageDigest.getInstance(HASH_ALGORITHM)
        var input = (salt + password).toByteArray()
        var result = md.digest(input)
        repeat(ITERATIONS - 1) {
            md.reset()
            result = md.digest(salt.toByteArray() + result)
        }
        return Base64.getEncoder().encodeToString(result)
    }
}
