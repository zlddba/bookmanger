package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.entity.Book
import org.zl.team.entity.Supplier
import org.zl.team.service.BookService
import org.zl.team.service.GrossProfitService
import org.zl.team.service.PurchaseService
import org.zl.team.service.SaleService
import org.zl.team.service.SupplierService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GrossProfitServiceTest {

    private val testDbPath = "test_grossprofit.db"

    @BeforeAll
    fun setUp() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
        DatabaseInitializer.initForTest(testDbPath)

        SupplierService.create(Supplier("GP-SUP", "毛利测试供应商", null, null, null, null, null, null, null))
        BookService.create(Book("GP-BOOK1", null, "毛利测试书1", null, null, null, null, null, 100.0, 50, null, null, null, null))
        BookService.create(Book("GP-BOOK2", null, "毛利测试书2", null, null, null, null, null, 200.0, 30, null, null, null, null))
    }

    @AfterAll
    fun tearDown() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
    }

    @Test
    fun `gross profit calculates revenue minus cost`() {
        // 进价 50 × 10 = 500
        PurchaseService.register("GP-SUP", "GP-BOOK1", 10, 50.0, 1.0, "2024-06-01", null)
        // 售价 100 × 3 = 300
        SaleService.sell("GP-BOOK1", 3, null, "2024-06-01", null)

        val rows = GrossProfitService.query("2024-06-01", "2024-06-01")
        assertEquals(1, rows.size)

        val row = rows[0]
        assertEquals("GP-BOOK1", row.bookId)
        assertEquals(3, row.quantity)
        assertEquals(100.0, row.unitPrice, 0.01)
        assertEquals(300.0, row.saleAmount, 0.01)
        assertEquals(50.0, row.costPrice, 0.01)
        assertEquals(150.0, row.cost, 0.01)       // 3 × 50
        assertEquals(150.0, row.grossProfit, 0.01) // 300 - 150
    }

    @Test
    fun `gross profit handles multiple sales`() {
        PurchaseService.register("GP-SUP", "GP-BOOK2", 5, 80.0, 1.0, "2024-06-10", null)  // 进货 80×5=400
        SaleService.sell("GP-BOOK2", 2, null, "2024-06-10", null)                           // 销售 200×2=400
        SaleService.sell("GP-BOOK2", 1, null, "2024-06-11", null)                           // 销售 200×1=200

        val rows = GrossProfitService.query("2024-06-10", "2024-06-11")
        assertEquals(2, rows.size)

        val totalSale = rows.sumOf { it.saleAmount }
        val totalCost = rows.sumOf { it.cost }
        val totalProfit = rows.sumOf { it.grossProfit }

        assertEquals(600.0, totalSale, 0.01)
        assertEquals(240.0, totalCost, 0.01)       // (2+1) × 80
        assertEquals(360.0, totalProfit, 0.01)     // 600 - 240
    }

    @Test
    fun `gross profit with no purchase price defaults to zero cost`() {
        // 没有进货记录的图书销售（进价为 0）
        BookService.create(Book("GP-NOPRICE", null, "无进价书", null, null, null, null, null, 50.0, 10, null, null, null, null))
        SaleService.sell("GP-NOPRICE", 2, null, "2024-08-01", null)

        val rows = GrossProfitService.query("2024-08-01", "2024-08-01")
        assertEquals(1, rows.size)
        assertEquals(0.0, rows[0].costPrice, 0.01)
        assertEquals(0.0, rows[0].cost, 0.01)
        assertEquals(100.0, rows[0].grossProfit, 0.01)
    }

    @Test
    fun `empty date range returns empty list`() {
        val rows = GrossProfitService.query("2099-01-01", "2099-12-31")
        assertEquals(0, rows.size)
    }

    @Test
    fun `gross profit row contains book title`() {
        PurchaseService.register("GP-SUP", "GP-BOOK1", 5, 50.0, 1.0, "2024-09-01", null)
        SaleService.sell("GP-BOOK1", 1, null, "2024-09-01", null)

        val rows = GrossProfitService.query("2024-09-01", "2024-09-01")
        assertEquals(1, rows.size)
        assertEquals("毛利测试书1", rows[0].bookTitle)
    }
}
