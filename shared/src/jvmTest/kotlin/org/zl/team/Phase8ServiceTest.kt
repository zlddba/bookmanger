package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.entity.Book
import org.zl.team.entity.Member
import org.zl.team.entity.MemberPolicy
import org.zl.team.service.BookService
import org.zl.team.service.MemberPolicyService
import org.zl.team.service.MemberService
import org.zl.team.service.SaleService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Phase8ServiceTest {

    private val testDbPath = "test_phase8.db"

    @BeforeAll
    fun setUp() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
        DatabaseInitializer.initForTest(testDbPath)
    }

    @AfterAll
    fun tearDown() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
    }

    // ========== MemberService 测试 ==========

    @Test
    fun `create and retrieve member`() {
        assertTrue(MemberService.create(
            Member("M001", 1, "张三", "男", "北京市", "清华", "010-11111111", "zhang@test.com", "学无止境", "2024-01-01"), "123456"))

        val found = MemberService.getById("M001")
        assertNotNull(found)
        assertEquals("张三", found!!.name)
        assertEquals(1, found.level)
        assertEquals("北京市", found.address)
    }

    @Test
    fun `create duplicate member returns false`() {
        assertTrue(MemberService.create(
            Member("M002", 1, "李四", null, null, null, null, null, null, null), "123456"))
        assertFalse(MemberService.create(
            Member("M002", 1, "重复", null, null, null, null, null, null, null), "654321"))
    }

    @Test
    fun `listAll returns members`() {
        MemberService.create(Member("M003", 1, "王五", null, null, null, null, null, null, null), "123456")
        MemberService.create(Member("M004", 1, "赵六", null, null, null, null, null, null, null), "123456")
        val all = MemberService.listAll()
        val ids = all.map { it.cardNo }
        assertTrue(ids.contains("M003"))
        assertTrue(ids.contains("M004"))
    }

    @Test
    fun `update member`() {
        MemberService.create(Member("M005", 1, "旧名", "女", "旧地址", null, null, null, null, null), "123456")
        val updated = Member("M005", 2, "新名", "男", "新地址", "新单位", "010-99999999", "new@test.com", "新格言", null)
        assertTrue(MemberService.update(updated))

        val found = MemberService.getById("M005")
        assertEquals("新名", found!!.name)
        assertEquals(2, found.level)
        assertEquals("新地址", found.address)
    }

    @Test
    fun `update nonexistent member returns false`() {
        assertFalse(MemberService.update(Member("NOPE", 1, "x", null, null, null, null, null, null, null)))
    }

    @Test
    fun `delete member removes both member and admin records`() {
        MemberService.create(Member("M006", 1, "待删", null, null, null, null, null, null, null), "123456")
        assertNotNull(MemberService.getById("M006"))
        assertTrue(MemberService.delete("M006"))
        assertNull(MemberService.getById("M006"))
    }

    @Test
    fun `changePassword succeeds with correct old password`() {
        MemberService.create(Member("M007", 1, "密码测试", null, null, null, null, null, null, null), "oldpass")
        assertTrue(MemberService.changePassword("M007", "oldpass", "newpass"))
    }

    @Test
    fun `changePassword fails with wrong old password`() {
        MemberService.create(Member("M008", 1, "密码测试2", null, null, null, null, null, null, null), "oldpass")
        assertFalse(MemberService.changePassword("M008", "wrong", "newpass"))
    }

    @Test
    fun `getDiscount returns correct discount for member level`() {
        MemberService.create(Member("M009", 3, "折扣测试", null, null, null, null, null, null, null), "123456")
        val discount = MemberService.getDiscount("M009")
        assertEquals(0.90, discount)
    }

    @Test
    fun `getDiscount returns 1 for member level 1`() {
        MemberService.create(Member("M010", 1, "无折扣", null, null, null, null, null, null, null), "123456")
        val discount = MemberService.getDiscount("M010")
        assertEquals(1.0, discount)
    }

    @Test
    fun `getDiscount returns 1 for nonexistent card`() {
        assertEquals(1.0, MemberService.getDiscount("NOPE"))
    }

    // ========== SaleService 测试 ==========

    @Test
    fun `sell without member uses full price`() {
        BookService.create(Book("S-BOOK1", null, "销售测试书1", null, null, null, null, null, 50.0, 20, null, null, null, null))

        val result = SaleService.sell("S-BOOK1", 3, null, "2024-01-15", null)
        assertTrue(result)

        // 库存从 20 变为 17
        val book = BookService.getById("S-BOOK1")
        assertEquals(17, book!!.stock)

        // 验证销售记录
        val all = SaleService.listAll()
        val sale = all.find { it.bookId == "S-BOOK1" }
        assertNotNull(sale)
        assertEquals(3, sale!!.quantity)
        assertEquals(1.0, sale.discount)
        assertEquals(150.0, sale.amount)
    }

    @Test
    fun `sell with member applies discount`() {
        BookService.create(Book("S-BOOK2", null, "销售测试书2", null, null, null, null, null, 100.0, 10, null, null, null, null))
        MemberService.create(Member("V001", 2, "VIP", null, null, null, null, null, null, null), "123456")

        val result = SaleService.sell("S-BOOK2", 2, "V001", "2024-01-15", null)
        assertTrue(result)

        val book = BookService.getById("S-BOOK2")
        assertEquals(8, book!!.stock)

        val sale = SaleService.listAll().find { it.bookId == "S-BOOK2" }
        assertNotNull(sale)
        assertEquals(0.95, sale!!.discount)
        assertEquals(190.0, sale.amount) // 2 * 100 * 0.95
    }

    @Test
    fun `sell with nonexistent book returns false`() {
        assertFalse(SaleService.sell("NO-BOOK", 1, null, "2024-01-01", null))
    }

    @Test
    fun `sell with insufficient stock returns false`() {
        BookService.create(Book("S-BOOK3", null, "库存不足书", null, null, null, null, null, 30.0, 2, null, null, null, null))
        assertFalse(SaleService.sell("S-BOOK3", 10, null, "2024-01-01", null))

        // 库存未变
        val book = BookService.getById("S-BOOK3")
        assertEquals(2, book!!.stock)
    }

    @Test
    fun `sell with nonexistent member card returns false`() {
        BookService.create(Book("S-BOOK4", null, "坏会员测试", null, null, null, null, null, 30.0, 5, null, null, null, null))
        assertFalse(SaleService.sell("S-BOOK4", 1, "BAD-CARD", "2024-01-01", null))
    }

    @Test
    fun `listByDate filters sales correctly`() {
        BookService.create(Book("S-BOOK5", null, "日期筛选", null, null, null, null, null, 20.0, 10, null, null, null, null))
        SaleService.sell("S-BOOK5", 1, null, "2024-03-01", null)
        SaleService.sell("S-BOOK5", 1, null, "2024-06-01", null)

        val march = SaleService.listByDate("2024-03-01")
        assertEquals(1, march.size)
        val june = SaleService.listByDate("2024-06-01")
        assertEquals(1, june.size)
        val empty = SaleService.listByDate("2024-12-01")
        assertEquals(0, empty.size)
    }

    // ========== MemberPolicyService 测试 ==========

    @Test
    fun `listAll returns initial 5 policies`() {
        val all = MemberPolicyService.listAll()
        assertTrue(all.size >= 5)
        assertNotNull(all.find { it.level == 1 })
    }

    @Test
    fun `getByLevel returns correct policy`() {
        val p = MemberPolicyService.getByLevel(3)
        assertNotNull(p)
        assertEquals(3, p!!.level)
        assertEquals(1000, p.minAmount)
        assertEquals("0.90", p.discount)
    }

    @Test
    fun `update policy`() {
        assertTrue(MemberPolicyService.update(MemberPolicy(5, 5000, "0.70", "测试赠品", "测试备注")))
        val p = MemberPolicyService.getByLevel(5)
        assertEquals("0.70", p!!.discount)
        assertEquals("测试赠品", p.gift)
        assertEquals("测试备注", p.remark)
    }

    @Test
    fun `update nonexistent policy returns false`() {
        assertFalse(MemberPolicyService.update(MemberPolicy(99, 0, "1.0", null, null)))
    }

    @Test
    fun `create new policy`() {
        assertTrue(MemberPolicyService.create(MemberPolicy(6, 10000, "0.75", "VIP礼盒", "钻石会员")))
        val p = MemberPolicyService.getByLevel(6)
        assertNotNull(p)
        assertEquals(10000, p!!.minAmount)
        assertEquals("0.75", p.discount)
        assertEquals("VIP礼盒", p.gift)
    }

    @Test
    fun `create duplicate policy level returns false`() {
        assertFalse(MemberPolicyService.create(MemberPolicy(1, 0, "1.0", null, null)))
    }

    // ========== 会员升级（total_spent）测试 ==========

    @Test
    fun `member totalSpent increases after sale and auto upgrades to level 2`() {
        BookService.create(Book("UP-BOOK1", null, "升级测试书1", null, null, null, null, null, 600.0, 10, null, null, null, null))
        MemberService.create(Member("UP001", 1, "升级会员", null, null, null, null, null, null, null), "123456")

        // 单次销售 600 元 → total_spent=600 → 升级到 level 2 (>=500)
        assertTrue(SaleService.sell("UP-BOOK1", 1, "UP001", "2024-01-15", null))

        val member = MemberService.getById("UP001")
        assertNotNull(member)
        assertTrue(member!!.totalSpent >= 600.0)
        assertEquals(2, member.level)
    }

    @Test
    fun `member auto upgrade skips levels when totalSpent jumps`() {
        BookService.create(Book("UP-BOOK2", null, "升级测试书2", null, null, null, null, null, 1200.0, 10, null, null, null, null))
        MemberService.create(Member("UP002", 1, "跳级会员", null, null, null, null, null, null, null), "123456")

        // 一次销售 1200 元 → total_spent=1200 → 直接到 level 3 (>=1000)
        assertTrue(SaleService.sell("UP-BOOK2", 1, "UP002", "2024-01-15", null))

        val member = MemberService.getById("UP002")
        assertEquals(3, member!!.level)
    }

    @Test
    fun `member stays at level 1 when totalSpent below threshold`() {
        BookService.create(Book("UP-BOOK3", null, "不升级书", null, null, null, null, null, 100.0, 10, null, null, null, null))
        MemberService.create(Member("UP003", 1, "不升级会员", null, null, null, null, null, null, null), "123456")

        // 销售 100 元 → total_spent=100 → 不满足 level 2 (>=500)
        assertTrue(SaleService.sell("UP-BOOK3", 1, "UP003", "2024-01-15", null))

        val member = MemberService.getById("UP003")
        assertEquals(1, member!!.level)
        assertEquals(100.0, member.totalSpent, 0.01)
    }

    // ========== BookService.search 测试 ==========

    @Test
    fun `search by title keyword`() {
        BookService.create(Book("SR01", null, "数据结构与算法", null, "严蔚敏", "清华大学出版社", null, "1111111111", 49.0, 5, null, "数据结构,算法", null, null))
        BookService.create(Book("SR02", null, "Java编程思想", null, "Bruce Eckel", "机械工业出版社", null, "2222222222", 79.0, 3, null, "Java,编程", null, null))

        val r = BookService.search("Java")
        assertEquals(1, r.size)
        assertEquals("Java编程思想", r[0].title)
    }

    @Test
    fun `search nonexistent keyword returns empty`() {
        val r = BookService.search("xyz不存在的关键字123")
        assertEquals(0, r.size)
    }
}
