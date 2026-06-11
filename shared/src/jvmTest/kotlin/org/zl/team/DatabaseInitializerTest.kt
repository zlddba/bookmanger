package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseInitializerTest {

    private val testDbPath = "test_bookstore.db"

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
    fun `all 13 tables should exist`() {
        val tables = listOf(
            "admin", "book", "supplier", "member", "member_policy",
            "purchase_record", "feedback", "sale_record", "bookstore_info",
            "book_category", "book_price", "return_record", "employee"
        )

        DatabaseInitializer.getConnection().use { conn ->
            for (table in tables) {
                val rs = conn.createStatement().executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='$table'"
                )
                assertTrue(rs.next(), "Table '$table' should exist")
                rs.close()
            }
        }
    }

    @Test
    fun `default admin accounts should exist`() {
        DatabaseInitializer.getConnection().use { conn ->
            val rs = conn.createStatement().executeQuery(
                "SELECT user_id, password, role FROM admin"
            )
            val rows = mutableListOf<Triple<String, String, String>>()
            while (rs.next()) {
                rows.add(Triple(rs.getString(1), rs.getString(2), rs.getString(3)))
            }
            rs.close()

            assertTrue(rows.any { it.first == "admin" && it.second == "admin" && it.third == "经理" })
            assertTrue(rows.any { it.first == "仓库管理员" && it.third == "仓库管理员" })
            assertTrue(rows.any { it.first == "售书员" && it.third == "售书员" })
        }
    }

    @Test
    fun `default book categories should exist`() {
        DatabaseInitializer.getConnection().use { conn ->
            val rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM book_category"
            )
            assertTrue(rs.next())
            assertEquals(17, rs.getInt(1))
            rs.close()
        }
    }

    @Test
    fun `default member policies should exist`() {
        DatabaseInitializer.getConnection().use { conn ->
            val rs = conn.createStatement().executeQuery(
                "SELECT COUNT(*) FROM member_policy"
            )
            assertTrue(rs.next())
            assertEquals(5, rs.getInt(1))
            rs.close()
        }
    }

    @Test
    fun `default bookstore info should exist`() {
        DatabaseInitializer.getConnection().use { conn ->
            val rs = conn.createStatement().executeQuery(
                "SELECT name FROM bookstore_info WHERE name = '内电子信息学院书店'"
            )
            assertTrue(rs.next())
            rs.close()
        }
    }
}
