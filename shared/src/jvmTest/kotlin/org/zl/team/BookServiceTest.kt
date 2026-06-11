package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.entity.Book
import org.zl.team.service.BookService
import org.zl.team.service.BookstoreInfoService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookServiceTest {

    private val testDbPath = "test_book.db"

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
    fun `create and retrieve book`() {
        val book = Book("B001", null, "测试书名", "测试丛书",
            "测试作者", "测试出版社", "第1版", "978-7-302-32332-3",
            39.9, 10, "测试简介", "测试,关键字", "2024-01-01", null)
        assertTrue(BookService.create(book))

        val found = BookService.getById("B001")
        assertNotNull(found)
        assertEquals("测试书名", found!!.title)
        assertEquals("测试作者", found.author)
        assertEquals(39.9, found.price)
        assertEquals(10, found.stock)
    }

    @Test
    fun `create duplicate book id returns false`() {
        val book = Book("B002", null, "书1", null, null, null, null, null, null, 0, null, null, null, null)
        assertTrue(BookService.create(book))
        assertFalse(BookService.create(book))
    }

    @Test
    fun `listAll returns all created books`() {
        BookService.create(Book("BT01", null, "自检书1", null, null, null, null, null, null, 0, null, null, null, null))
        BookService.create(Book("BT02", null, "自检书2", null, null, null, null, null, null, 0, null, null, null, null))
        val all = BookService.listAll()
        val ids = all.map { it.bookId }
        assertTrue(ids.contains("BT01"))
        assertTrue(ids.contains("BT02"))
    }

    @Test
    fun `update book fields`() {
        BookService.create(Book("B003", null, "旧书名", null, "旧作者", null, null, null, 10.0, 5, null, null, null, null))

        val updated = Book("B003", null, "新书名", null, "新作者", "新出版社", "第2版", "730208889X", 20.0, 8, "新简介", "新关键词", "2024-06-01", null)
        assertTrue(BookService.update(updated))

        val found = BookService.getById("B003")
        assertEquals("新书名", found!!.title)
        assertEquals("新作者", found.author)
        assertEquals("新出版社", found.publisher)
        assertEquals(20.0, found.price)
        assertEquals(8, found.stock)
    }

    @Test
    fun `update nonexistent book returns false`() {
        assertFalse(BookService.update(Book("NOPE", null, "x", null, null, null, null, null, null, 0, null, null, null, null)))
    }

    @Test
    fun `delete book`() {
        BookService.create(Book("B004", null, "待删", null, null, null, null, null, null, 0, null, null, null, null))
        assertTrue(BookService.delete("B004"))
        assertNull(BookService.getById("B004"))
    }

    @Test
    fun `delete nonexistent book returns false`() {
        assertFalse(BookService.delete("NOPE"))
    }

    @Test
    fun `search by keyword finds matching books`() {
        BookService.create(Book("B005", null, "Java编程", null, "张三", "清华出版社", null, "1111111111", 50.0, 3, null, "Java,编程", null, null))
        BookService.create(Book("B006", null, "Kotlin实战", null, "李四", "人民邮电", null, "2222222222", 60.0, 5, null, "Kotlin", null, null))
        BookService.create(Book("B007", null, "Python基础", null, "王五", "机械工业", null, "3333333333", 40.0, 2, null, "Python", null, null))

        val results = BookService.search("Java")
        assertEquals(1, results.size)
        assertEquals("Java编程", results[0].title)
    }

    @Test
    fun `search nonexistent keyword returns empty`() {
        val results = BookService.search("不存在的关键字xyz")
        assertEquals(0, results.size)
    }

    @Test
    fun `bookstore info get and update`() {
        val info = BookstoreInfoService.getInfo()
        assertNotNull(info)
        assertEquals("内电子信息学院书店", info!!.name)

        val updated = info.copy(address = "新地址", phone = "010-99999999")
        assertDoesNotThrow { BookstoreInfoService.update(updated) }

        val reloaded = BookstoreInfoService.getInfo()
        assertEquals("新地址", reloaded!!.address)
        assertEquals("010-99999999", reloaded.phone)
    }
}
