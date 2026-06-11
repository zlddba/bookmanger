package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.entity.Book
import org.zl.team.entity.Supplier
import org.zl.team.service.BookService
import org.zl.team.service.PurchaseService
import org.zl.team.service.ReturnService
import org.zl.team.service.SaleService
import org.zl.team.service.SettlementService
import org.zl.team.service.SupplierService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SettlementServiceTest {

    private val testDbPath = "test_settlement.db"

    @BeforeAll
    fun setUp() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
        DatabaseInitializer.initForTest(testDbPath)

        SupplierService.create(Supplier("STL-SUP", "结算测试供应商", null, null, null, null, null, null, null))
        BookService.create(Book("STL-BOOK", null, "结算测试书", null, null, null, null, null, 100.0, 50, null, null, null, null))
    }

    @AfterAll
    fun tearDown() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
    }

    @Test
    fun `daily settlement groups by day`() {
        PurchaseService.register("STL-SUP", "STL-BOOK", 10, 50.0, 1.0, "2024-06-01", null)  // 500
        SaleService.sell("STL-BOOK", 3, null, "2024-06-01", null)                            // 300
        SaleService.sell("STL-BOOK", 2, null, "2024-06-02", null)                            // 200

        val rows = SettlementService.dailySettlement("2024-06-01", "2024-06-02")
        assertEquals(2, rows.size)

        val day1 = rows.first { it.period == "2024-06-01" }
        assertEquals(300.0, day1.saleAmount, 0.01)
        assertEquals(1, day1.saleCount)
        assertEquals(500.0, day1.purchaseAmount, 0.01)

        val day2 = rows.first { it.period == "2024-06-02" }
        assertEquals(200.0, day2.saleAmount, 0.01)
    }

    @Test
    fun `monthly settlement groups by month`() {
        PurchaseService.register("STL-SUP", "STL-BOOK", 5, 40.0, 1.0, "2024-03-10", null)  // 200
        SaleService.sell("STL-BOOK", 2, null, "2024-03-15", null)                            // 200
        SaleService.sell("STL-BOOK", 1, null, "2024-04-20", null)                            // 100

        val rows = SettlementService.monthlySettlement("2024-03-01", "2024-04-30")
        assertEquals(2, rows.size)

        val mar = rows.first { it.period == "2024-03" }
        assertEquals(200.0, mar.saleAmount, 0.01)
        assertEquals(200.0, mar.purchaseAmount, 0.01)

        val apr = rows.first { it.period == "2024-04" }
        assertEquals(100.0, apr.saleAmount, 0.01)
        assertEquals(0.0, apr.purchaseAmount, 0.01)
    }

    @Test
    fun `settlement profit calculation is correct`() {
        // 进价 50 × 5 = 250 成本
        PurchaseService.register("STL-SUP", "STL-BOOK", 5, 50.0, 1.0, "2024-05-10", null)
        // 售价 100 × 4 = 400 收入（无折扣）
        SaleService.sell("STL-BOOK", 4, null, "2024-05-10", null)

        val rows = SettlementService.dailySettlement("2024-05-10", "2024-05-10")
        assertEquals(1, rows.size)
        // profit = 400 - 250 = 150
        assertEquals(150.0, rows[0].profit, 0.01)
    }

    @Test
    fun `settlement includes return recovery`() {
        PurchaseService.register("STL-SUP", "STL-BOOK", 10, 50.0, 1.0, "2024-07-01", null)       // 500 支出
        SaleService.sell("STL-BOOK", 3, null, "2024-07-01", null)                                  // 300 收入

        // 退货：unit_price = 50, quantity = 2, amount = 100
        val records = ReturnService.listAll()
        // 通过 PurchaseService 进货后库存增加了，先让库存有足够的量，然后测试退货
        // 直接使用 ReturnService.register
        ReturnService.register("STL-SUP", "STL-BOOK", 50.0, 2, "质量问题", "2024-07-01", null)

        val rows = SettlementService.dailySettlement("2024-07-01", "2024-07-01")
        assertEquals(1, rows.size)
        val row = rows[0]
        assertEquals(300.0, row.saleAmount, 0.01)
        assertEquals(500.0, row.purchaseAmount, 0.01)
        assertEquals(100.0, row.returnAmount, 0.01)
        // profit = 300 - 500 + 100 = -100
        assertEquals(-100.0, row.profit, 0.01)
    }

    @Test
    fun `empty date range returns empty list`() {
        val rows = SettlementService.dailySettlement("2099-01-01", "2099-12-31")
        assertEquals(0, rows.size)
    }
}
