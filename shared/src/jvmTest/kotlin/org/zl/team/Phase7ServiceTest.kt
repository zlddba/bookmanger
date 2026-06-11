package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.entity.Book
import org.zl.team.entity.BookCategory
import org.zl.team.entity.ReturnRecord
import org.zl.team.entity.Supplier
import org.zl.team.service.BookService
import org.zl.team.service.CategoryService
import org.zl.team.service.FeedbackService
import org.zl.team.service.PurchaseService
import org.zl.team.service.ReturnService
import org.zl.team.service.SaleService
import org.zl.team.service.StatisticsService
import org.zl.team.service.SupplierService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Phase7ServiceTest {

    private val testDbPath = "test_phase7.db"

    @BeforeAll
    fun setUp() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
        DatabaseInitializer.initForTest(testDbPath)
    }

    @AfterAll
    fun tearDown() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
    }

    // ========== SupplierService 测试 ==========

    @Test
    fun `create and retrieve supplier`() {
        val s = Supplier("SUP01", "测试出版社", "北京市", "http://test.com", "张三", "010-12345678", "010-87654321", "test@sup.com", "测试简介")
        assertTrue(SupplierService.create(s))

        val found = SupplierService.getById("SUP01")
        assertNotNull(found)
        assertEquals("测试出版社", found!!.name)
        assertEquals("北京市", found.address)
        assertEquals("张三", found.contact)
    }

    @Test
    fun `create duplicate supplier returns false`() {
        val s = Supplier("SUP02", "重复测试", null, null, null, null, null, null, null)
        assertTrue(SupplierService.create(s))
        assertFalse(SupplierService.create(s))
    }

    @Test
    fun `listAll returns suppliers`() {
        SupplierService.create(Supplier("SUP03", "出版社A", null, null, null, null, null, null, null))
        SupplierService.create(Supplier("SUP04", "出版社B", null, null, null, null, null, null, null))
        val all = SupplierService.listAll()
        val ids = all.map { it.supplierId }
        assertTrue(ids.contains("SUP03"))
        assertTrue(ids.contains("SUP04"))
    }

    @Test
    fun `update supplier`() {
        SupplierService.create(Supplier("SUP05", "旧名称", "旧地址", null, null, null, null, null, null))
        val updated = Supplier("SUP05", "新名称", "新地址", "new.com", "李四", "010-99999999", null, "new@sup.com", null)
        assertTrue(SupplierService.update(updated))

        val found = SupplierService.getById("SUP05")
        assertEquals("新名称", found!!.name)
        assertEquals("新地址", found.address)
        assertEquals("010-99999999", found.phone)
    }

    @Test
    fun `update nonexistent supplier returns false`() {
        assertFalse(SupplierService.update(Supplier("NOPE", "x", null, null, null, null, null, null, null)))
    }

    @Test
    fun `delete supplier`() {
        SupplierService.create(Supplier("SUP06", "待删除", null, null, null, null, null, null, null))
        assertTrue(SupplierService.delete("SUP06"))
        assertNull(SupplierService.getById("SUP06"))
    }

    @Test
    fun `delete nonexistent supplier returns false`() {
        assertFalse(SupplierService.delete("NOPE"))
    }

    // ========== CategoryService 测试 ==========

    @Test
    fun `listAll returns initial categories`() {
        val all = CategoryService.listAll()
        assertTrue(all.isNotEmpty())
        val ids = all.map { it.categoryId }
        assertTrue(ids.contains("CAT-01"))
        assertTrue(ids.contains("CAT-01-01"))
        assertTrue(ids.contains("CAT-02"))
    }

    @Test
    fun `getTopLevel returns only root categories`() {
        val top = CategoryService.getTopLevel()
        val ids = top.map { it.categoryId }
        assertTrue(ids.contains("CAT-01"))
        assertTrue(ids.contains("CAT-02"))
        assertTrue(ids.contains("CAT-03"))
        // 子分类不应该出现
        assertFalse(ids.contains("CAT-01-01"))
    }

    @Test
    fun `getChildren returns subcategories`() {
        val children = CategoryService.getChildren("CAT-01")
        val ids = children.map { it.categoryId }
        assertTrue(ids.contains("CAT-01-01"))
        assertTrue(ids.contains("CAT-01-02"))
        assertTrue(ids.contains("CAT-01-03"))
    }

    @Test
    fun `create new category`() {
        val cat = BookCategory("NEW-TOP", "新顶级分类", null)
        assertTrue(CategoryService.create(cat))

        val child = BookCategory("NEW-SUB", "新子分类", "NEW-TOP")
        assertTrue(CategoryService.create(child))

        val children = CategoryService.getChildren("NEW-TOP")
        assertEquals(1, children.size)
        assertEquals("新子分类", children[0].name)
    }

    @Test
    fun `create duplicate category returns false`() {
        assertFalse(CategoryService.create(BookCategory("CAT-01", "dup", null)))
    }

    @Test
    fun `update category`() {
        CategoryService.create(BookCategory("CAT-UPD", "旧分类名", null))
        assertTrue(CategoryService.update(BookCategory("CAT-UPD", "新分类名", "CAT-01")))

        val all = CategoryService.listAll()
        val updated = all.find { it.categoryId == "CAT-UPD" }
        assertEquals("新分类名", updated!!.name)
        assertEquals("CAT-01", updated.parentId)
    }

    @Test
    fun `update nonexistent category returns false`() {
        assertFalse(CategoryService.update(BookCategory("NOPE", "x", null)))
    }

    @Test
    fun `delete category cascades to children`() {
        CategoryService.create(BookCategory("PARENT", "父分类", null))
        CategoryService.create(BookCategory("CHILD1", "子1", "PARENT"))
        CategoryService.create(BookCategory("CHILD2", "子2", "PARENT"))

        assertTrue(CategoryService.delete("PARENT"))

        val all = CategoryService.listAll()
        val ids = all.map { it.categoryId }
        assertFalse(ids.contains("PARENT"))
        assertFalse(ids.contains("CHILD1"))
        assertFalse(ids.contains("CHILD2"))
    }

    // ========== PurchaseService 测试 ==========

    @Test
    fun `purchase register updates stock and price`() {
        SupplierService.create(Supplier("P-SUP", "进货供应商", null, null, null, null, null, null, null))
        BookService.create(Book("P-BOOK", "CAT-01-01", "进货测试书", null, null, null, null, null, 50.0, 10, null, null, null, null))

        val result = PurchaseService.register("P-SUP", "P-BOOK", 5, 30.0, 1.0, "2024-01-15", null)
        assertTrue(result)

        // 验证库存从 10 变为 15
        val book = BookService.getById("P-BOOK")
        assertEquals(15, book!!.stock)
    }

    @Test
    fun `purchase register with nonexistent book returns false`() {
        val result = PurchaseService.register("P-SUP", "NO-BOOK", 5, 30.0, 1.0, "2024-01-15", null)
        assertFalse(result)
    }

    @Test
    fun `listAll purchases after register`() {
        val all = PurchaseService.listAll()
        val bookIds = all.map { it.bookId }
        assertTrue(bookIds.contains("P-BOOK"))
    }

    @Test
    fun `listByDateRange filters correctly`() {
        SupplierService.create(Supplier("P-SUP2", "日期测试供应商", null, null, null, null, null, null, null))
        BookService.create(Book("P-BOOK2", null, "日期测试书", null, null, null, null, null, 40.0, 5, null, null, null, null))

        PurchaseService.register("P-SUP2", "P-BOOK2", 3, 25.0, 1.0, "2024-03-15", null)
        PurchaseService.register("P-SUP2", "P-BOOK2", 2, 25.0, 1.0, "2024-06-20", null)

        val inRange = PurchaseService.listByDateRange("2024-01-01", "2024-04-30")
        val outRange = PurchaseService.listByDateRange("2024-07-01", "2024-12-31")

        assertTrue(inRange.isNotEmpty())
        assertTrue(outRange.isEmpty())
    }

    // ========== ReturnService 测试 ==========

    @Test
    fun `return register deducts stock`() {
        SupplierService.create(Supplier("R-SUP", "退货供应商", null, null, null, null, null, null, null))
        BookService.create(Book("R-BOOK", null, "退货测试书", null, null, null, null, null, 30.0, 20, null, null, null, null))

        val result = ReturnService.register("R-SUP", "R-BOOK", 30.0, 5, "质量问题", "2024-01-20", null)
        assertTrue(result)

        // 验证库存从 20 变为 15
        val book = BookService.getById("R-BOOK")
        assertEquals(15, book!!.stock)
    }

    @Test
    fun `return register with insufficient stock returns false`() {
        SupplierService.create(Supplier("R-SUP2", "退货供应商2", null, null, null, null, null, null, null))
        BookService.create(Book("R-BOOK2", null, "库存不足测试", null, null, null, null, null, 30.0, 3, null, null, null, null))

        // 退货数量 10 > 库存 3
        val result = ReturnService.register("R-SUP2", "R-BOOK2", 30.0, 10, "退货", "2024-01-20", null)
        assertFalse(result)

        // 库存未变
        val book = BookService.getById("R-BOOK2")
        assertEquals(3, book!!.stock)
    }

    @Test
    fun `return register with nonexistent book returns false`() {
        val result = ReturnService.register("R-SUP", "NO-BOOK", 30.0, 1, "test", "2024-01-01", null)
        assertFalse(result)
    }

    @Test
    fun `listAll returns return records`() {
        val all = ReturnService.listAll()
        val bookIds = all.map { it.bookId }
        assertTrue(bookIds.contains("R-BOOK"))
    }

    @Test
    fun `listByDateRange filters return records`() {
        ReturnService.register("R-SUP", "R-BOOK", 30.0, 2, "测试1", "2024-02-10", null)
        ReturnService.register("R-SUP", "R-BOOK", 30.0, 1, "测试2", "2024-08-15", null)

        val inRange = ReturnService.listByDateRange("2024-01-01", "2024-04-30")
        val outRange = ReturnService.listByDateRange("2024-09-01", "2024-12-31")

        assertTrue(inRange.any { it.reason == "测试1" })
        assertTrue(outRange.isEmpty() || outRange.none { it.reason == "测试1" || it.reason == "测试2" })
    }

    // ========== FeedbackService 测试 ==========

    @Test
    fun `listAll feedback returns empty initially`() {
        val all = FeedbackService.listAll()
        assertTrue(all.isEmpty())
    }

    // ========== StatisticsService 测试 ==========

    @Test
    fun `statistics by day correctly aggregates purchase sale and return`() {
        SupplierService.create(Supplier("STAT-SUP", "统计供应商", null, null, null, null, null, null, null))
        BookService.create(Book("STAT-BOOK", null, "统计测试书", null, null, null, null, null, 100.0, 50, null, null, null, null))

        // 进货 10 本 × 50 元 = 500
        PurchaseService.register("STAT-SUP", "STAT-BOOK", 10, 50.0, 1.0, "2024-06-01", null)
        // 销售 2 本 × 100 元 = 200
        SaleService.sell("STAT-BOOK", 2, null, "2024-06-01", null)
        // 退货 1 本 × 50 元 = 50
        ReturnService.register("STAT-SUP", "STAT-BOOK", 50.0, 1, "测试", "2024-06-01", null)

        val stats = StatisticsService.statistics("2024-06-01", "2024-06-01", "日")
        assertEquals(1, stats.size)
        val row = stats[0]
        assertEquals("2024-06-01", row.period)
        assertEquals(500.0, row.purchaseAmount, 0.01)
        assertEquals(200.0, row.saleAmount, 0.01)
        assertEquals(50.0, row.returnAmount, 0.01)
        // profit = sale - purchase + return = 200 - 500 + 50 = -250
        assertEquals(-250.0, row.profit, 0.01)
    }

    @Test
    fun `statistics by month groups correctly`() {
        SupplierService.create(Supplier("STAT-SUP2", "月统计供应商", null, null, null, null, null, null, null))
        BookService.create(Book("STAT-BOOK2", null, "月统计测试书", null, null, null, null, null, 50.0, 30, null, null, null, null))

        PurchaseService.register("STAT-SUP2", "STAT-BOOK2", 5, 30.0, 1.0, "2024-11-10", null) // 150
        SaleService.sell("STAT-BOOK2", 3, null, "2024-11-20", null) // 150
        SaleService.sell("STAT-BOOK2", 1, null, "2024-11-25", null) // 50

        val stats = StatisticsService.statistics("2024-11-01", "2024-11-30", "月")
        assertEquals(1, stats.size)
        assertEquals("2024-11", stats[0].period)
        assertEquals(150.0, stats[0].purchaseAmount, 0.01)
        assertEquals(200.0, stats[0].saleAmount, 0.01)
    }

    @Test
    fun `statistics empty date range returns empty list`() {
        val stats = StatisticsService.statistics("2099-01-01", "2099-12-31", "日")
        assertEquals(0, stats.size)
    }
}
