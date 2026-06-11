package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.entity.Book
import org.zl.team.entity.BorrowRecord
import org.zl.team.entity.Member
import org.zl.team.service.BookService
import org.zl.team.service.BorrowService
import org.zl.team.service.MemberService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BorrowServiceTest {

    private val testDbPath = "test_borrow.db"

    @BeforeAll
    fun setUp() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
        DatabaseInitializer.initForTest(testDbPath)
        // 准备测试数据：会员和图书
        MemberService.create(Member(cardNo = "M001", name = "测试会员", level = 1, gender = null, address = null, company = null, phone = null, email = null, motto = null, regDate = "2026-06-01"), "123456")
        MemberService.create(Member(cardNo = "M002", name = "测试会员2", level = 1, gender = null, address = null, company = null, phone = null, email = null, motto = null, regDate = "2026-06-01"), "123456")
        BookService.create(Book(bookId = "B-TEST-1", title = "测试图书1", categoryId = "CAT-01-01", series = null, author = "作者1", publisher = "出版社1", edition = null, isbn = null, price = 50.0, stock = 5, description = null, keywords = null, publishDate = null, createdAt = null))
        BookService.create(Book(bookId = "B-TEST-2", title = "测试图书2", categoryId = "CAT-01-01", series = null, author = "作者2", publisher = "出版社2", edition = null, isbn = null, price = 60.0, stock = 3, description = null, keywords = null, publishDate = null, createdAt = null))
        BookService.create(Book(bookId = "B-TEST-3", title = "测试图书3", categoryId = "CAT-01-01", series = null, author = "作者3", publisher = "出版社3", edition = null, isbn = null, price = 70.0, stock = 1, description = null, keywords = null, publishDate = null, createdAt = null))
        BookService.create(Book(bookId = "B-NOSTOCK", title = "零库存书", categoryId = "CAT-01-01", series = null, author = "作者4", publisher = "出版社4", edition = null, isbn = null, price = 40.0, stock = 0, description = null, keywords = null, publishDate = null, createdAt = null))
    }

    @AfterAll
    fun tearDown() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
    }

    @Test
    fun `borrow succeeds and reduces stock`() {
        val before = BookService.getById("B-TEST-1")!!.stock
        assertTrue(BorrowService.borrow("B-TEST-1", "M001", "2026-06-05"))
        val after = BookService.getById("B-TEST-1")!!.stock
        assertEquals(before - 1, after)

        val all = BorrowService.listAll()
        val record = all.find { it.bookId == "B-TEST-1" && it.cardNo == "M001" }
        assertNotNull(record)
        assertEquals("2026-06-05", record!!.borrowDate)
        assertEquals("2026-07-05", record.dueDate) // +30天
        assertEquals("借阅中", record.status)
    }

    @Test
    fun `borrow with no stock returns false`() {
        assertFalse(BorrowService.borrow("B-NOSTOCK", "M001", "2026-06-05"))
    }

    @Test
    fun `borrow with nonexistent member returns false`() {
        assertFalse(BorrowService.borrow("B-TEST-1", "NOBODY", "2026-06-05"))
    }

    @Test
    fun `borrow exceeding 3-book limit returns false`() {
        assertTrue(BorrowService.borrow("B-TEST-1", "M002", "2026-06-01"))
        assertTrue(BorrowService.borrow("B-TEST-2", "M002", "2026-06-02"))
        assertTrue(BorrowService.borrow("B-TEST-3", "M002", "2026-06-03"))
        val count = BorrowService.getActiveBorrowCount("M002")
        assertEquals(3, count)
        // 第4本应该失败
        assertFalse(BorrowService.borrow("B-TEST-1", "M002", "2026-06-04"))
    }

    @Test
    fun `return book increases stock and updates status`() {
        BorrowService.borrow("B-TEST-1", "M001", "2026-06-05")
        val records = BorrowService.listByMember("M001")
        val borrowId = records.first { it.bookId == "B-TEST-1" }.id

        val before = BookService.getById("B-TEST-1")!!.stock
        assertTrue(BorrowService.returnBook(borrowId, "2026-06-10"))
        val after = BookService.getById("B-TEST-1")!!.stock
        assertEquals(before + 1, after)

        val updated = BorrowService.getById(borrowId)
        assertEquals("已归还", updated!!.status)
        assertEquals("2026-06-10", updated.returnDate)
    }

    @Test
    fun `return already returned book returns false`() {
        BorrowService.borrow("B-TEST-1", "M001", "2026-06-05")
        val records = BorrowService.listByMember("M001")
        val borrowId = records.first { it.bookId == "B-TEST-1" }.id
        assertTrue(BorrowService.returnBook(borrowId, "2026-06-10"))
        assertFalse(BorrowService.returnBook(borrowId, "2026-06-11"))
    }

    @Test
    fun `renew extends due date and increments renew count`() {
        BorrowService.borrow("B-TEST-1", "M001", "2026-06-05")
        val records = BorrowService.listByMember("M001")
        val borrowId = records.first { it.bookId == "B-TEST-1" }.id

        assertTrue(BorrowService.renew(borrowId))
        val updated = BorrowService.getById(borrowId)
        assertEquals("2026-08-04", updated!!.dueDate) // 原dueDate +30天
        assertEquals(1, updated.renewCount)
    }

    @Test
    fun `renew twice returns false`() {
        BorrowService.borrow("B-TEST-1", "M001", "2026-06-05")
        val records = BorrowService.listByMember("M001")
        val borrowId = records.first { it.bookId == "B-TEST-1" }.id

        assertTrue(BorrowService.renew(borrowId))
        assertFalse(BorrowService.renew(borrowId))
    }

    @Test
    fun `renew returned book returns false`() {
        BorrowService.borrow("B-TEST-1", "M001", "2026-06-05")
        val records = BorrowService.listByMember("M001")
        val borrowId = records.first { it.bookId == "B-TEST-1" }.id
        BorrowService.returnBook(borrowId, "2026-06-10")
        assertFalse(BorrowService.renew(borrowId))
    }

    @Test
    fun `listByMember filters correctly`() {
        BorrowService.borrow("B-TEST-1", "M001", "2026-06-05")
        BorrowService.borrow("B-TEST-2", "M002", "2026-06-06")
        val m1 = BorrowService.listByMember("M001")
        assertTrue(m1.all { it.cardNo == "M001" })
        assertTrue(m1.isNotEmpty())
    }

    @Test
    fun `listByStatus filters correctly`() {
        BorrowService.borrow("B-TEST-1", "M001", "2026-06-05")
        BorrowService.borrow("B-TEST-2", "M001", "2026-06-06")
        val records = BorrowService.listByMember("M001")
        val firstId = records.first().id
        BorrowService.returnBook(firstId, "2026-06-10")

        val active = BorrowService.listByStatus("借阅中")
        assertTrue(active.all { it.status == "借阅中" })

        val returned = BorrowService.listByStatus("已归还")
        assertTrue(returned.isNotEmpty())
        assertTrue(returned.all { it.status == "已归还" })
    }

    @Test
    fun `getActiveBorrowCount returns correct count`() {
        assertEquals(0, BorrowService.getActiveBorrowCount("M001"))
        BorrowService.borrow("B-TEST-1", "M001", "2026-06-05")
        assertEquals(1, BorrowService.getActiveBorrowCount("M001"))
        BorrowService.borrow("B-TEST-2", "M001", "2026-06-06")
        assertEquals(2, BorrowService.getActiveBorrowCount("M001"))
    }
}
