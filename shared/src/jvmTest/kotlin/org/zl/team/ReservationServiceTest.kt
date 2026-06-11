package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.entity.BookReservation
import org.zl.team.service.ReservationService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReservationServiceTest {

    private val testDbPath = "test_reservation.db"

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
    fun `create reservation and retrieve by status`() {
        val r = BookReservation(
            bookTitle = "深入理解Kotlin",
            author = "张三",
            publisher = "清华大学出版社",
            isbn = "978-7-302-12345-6",
            customerName = "李四",
            customerPhone = "13800138000",
            status = "待处理",
            note = "急需"
        )
        assertTrue(ReservationService.create(r))

        val pending = ReservationService.listByStatus("待处理")
        assertTrue(pending.any { it.bookTitle == "深入理解Kotlin" })
        assertEquals("李四", pending.first { it.bookTitle == "深入理解Kotlin" }.customerName)
    }

    @Test
    fun `listAll returns all reservations`() {
        ReservationService.create(BookReservation(bookTitle = "书A", customerName = "客户A"))
        ReservationService.create(BookReservation(bookTitle = "书B", customerName = "客户B", status = "待处理"))

        val all = ReservationService.listAll()
        assertTrue(all.size >= 2)
    }

    @Test
    fun `filter by status works`() {
        ReservationService.create(BookReservation(bookTitle = "待处理书", customerName = "测试1", status = "待处理"))
        ReservationService.create(BookReservation(bookTitle = "已到货书", customerName = "测试2", status = "已到货"))

        val pending = ReservationService.listByStatus("待处理")
        assertTrue(pending.all { it.status == "待处理" })

        val completed = ReservationService.listByStatus("已到货")
        assertTrue(completed.all { it.status == "已到货" })
    }

    @Test
    fun `mark reservation as completed updates status and resolvedAt`() {
        ReservationService.create(BookReservation(bookTitle = "待标记书", customerName = "测试"))

        val all = ReservationService.listAll()
        val target = all.first { it.bookTitle == "待标记书" }
        assertEquals("待处理", target.status)
        assertNull(target.resolvedAt)

        assertTrue(ReservationService.markCompleted(target.id))

        val updated = ReservationService.listAll().first { it.id == target.id }
        assertEquals("已到货", updated.status)
        assertNotNull(updated.resolvedAt)
    }

    @Test
    fun `cancel reservation updates status to cancelled`() {
        ReservationService.create(BookReservation(bookTitle = "要取消的书", customerName = "测试"))

        val all = ReservationService.listAll()
        val target = all.first { it.bookTitle == "要取消的书" }
        assertEquals("待处理", target.status)

        assertTrue(ReservationService.cancel(target.id))

        val updated = ReservationService.listAll().first { it.id == target.id }
        assertEquals("已取消", updated.status)
        assertNotNull(updated.resolvedAt)
    }

    @Test
    fun `reservation with optional fields null works`() {
        val r = BookReservation(
            bookTitle = "最简预订",
            customerName = "王五"
        )
        assertTrue(ReservationService.create(r))

        val found = ReservationService.listByStatus("待处理").first { it.bookTitle == "最简预订" }
        assertEquals("王五", found.customerName)
        assertNull(found.author)
        assertNull(found.publisher)
        assertNull(found.isbn)
        assertNull(found.customerPhone)
    }
}
