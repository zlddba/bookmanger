package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.config.MyBatisUtil
import org.zl.team.entity.*
import org.zl.team.mapper.*
import org.apache.ibatis.session.SqlSession
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.nio.file.Files
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class MapperIntegrationTest {

    private val testDbPath = "test_mapper_integration.db"
    private lateinit var session: SqlSession

    private lateinit var adminMapper: AdminMapper
    private lateinit var employeeMapper: EmployeeMapper
    private lateinit var supplierMapper: SupplierMapper
    private lateinit var categoryMapper: BookCategoryMapper
    private lateinit var bookstoreInfoMapper: BookstoreInfoMapper
    private lateinit var bookMapper: BookMapper
    private lateinit var bookPriceMapper: BookPriceMapper
    private lateinit var purchaseRecordMapper: PurchaseRecordMapper
    private lateinit var returnRecordMapper: ReturnRecordMapper
    private lateinit var saleRecordMapper: SaleRecordMapper
    private lateinit var memberMapper: MemberMapper
    private lateinit var memberPolicyMapper: MemberPolicyMapper
    private lateinit var feedbackMapper: FeedbackMapper

    @BeforeAll
    fun setUp() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
        DatabaseInitializer.initForTest(testDbPath)
        session = MyBatisUtil.getSqlSession()

        adminMapper = session.getMapper(AdminMapper::class.java)
        employeeMapper = session.getMapper(EmployeeMapper::class.java)
        supplierMapper = session.getMapper(SupplierMapper::class.java)
        categoryMapper = session.getMapper(BookCategoryMapper::class.java)
        bookstoreInfoMapper = session.getMapper(BookstoreInfoMapper::class.java)
        bookMapper = session.getMapper(BookMapper::class.java)
        bookPriceMapper = session.getMapper(BookPriceMapper::class.java)
        purchaseRecordMapper = session.getMapper(PurchaseRecordMapper::class.java)
        returnRecordMapper = session.getMapper(ReturnRecordMapper::class.java)
        saleRecordMapper = session.getMapper(SaleRecordMapper::class.java)
        memberMapper = session.getMapper(MemberMapper::class.java)
        memberPolicyMapper = session.getMapper(MemberPolicyMapper::class.java)
        feedbackMapper = session.getMapper(FeedbackMapper::class.java)
    }

    @AfterAll
    fun tearDown() {
        session.close()
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
    }

    // ==================== Admin ====================

    @Test @Order(1)
    fun `admin selectById should return default admin`() {
        val admin = adminMapper.selectById("admin")
        assertNotNull(admin)
        assertEquals("admin", admin!!.password)
        assertEquals("经理", admin.role)
    }

    @Test @Order(2)
    fun `admin insert and delete`() {
        val testAdmin = Admin("test_user", "123456", "游客")
        adminMapper.insert(testAdmin)
        session.commit()

        var found = adminMapper.selectById("test_user")
        assertNotNull(found)
        assertEquals("123456", found!!.password)

        adminMapper.delete("test_user")
        session.commit()
        found = adminMapper.selectById("test_user")
        assertNull(found)
    }

    // ==================== Employee ====================

    @Test @Order(3)
    fun `employee insert select update delete`() {
        val emp = Employee("EMP001", "张三", "男", "北京", "010-123", "138000", "z@test.com", "天道酬勤", null)
        employeeMapper.insert(emp)
        session.commit()

        var found = employeeMapper.selectById("EMP001")
        assertNotNull(found)
        assertEquals("张三", found!!.name)

        employeeMapper.update(emp.copy(name = "张三丰"))
        session.commit()
        found = employeeMapper.selectById("EMP001")
        assertEquals("张三丰", found!!.name)

        employeeMapper.delete("EMP001")
        session.commit()
        found = employeeMapper.selectById("EMP001")
        assertNull(found)
    }

    // ==================== Supplier ====================

    @Test @Order(4)
    fun `supplier CRUD`() {
        val s = Supplier("SUP-001", "新华书店", "北京", "www.xh.com", "李四", "010-999", "", "xh@xh.com", "大型供应商")
        supplierMapper.insert(s); session.commit()

        var found = supplierMapper.selectById("SUP-001")
        assertNotNull(found)
        assertEquals("新华书店", found!!.name)

        supplierMapper.update(s.copy(name = "新华书店总店"))
        session.commit()
        found = supplierMapper.selectById("SUP-001")
        assertEquals("新华书店总店", found!!.name)

        supplierMapper.delete("SUP-001"); session.commit()
        found = supplierMapper.selectById("SUP-001")
        assertNull(found)
    }

    // ==================== BookCategory ====================

    @Test @Order(5)
    fun `category selectByParentId returns roots`() {
        val roots = categoryMapper.selectByParentId(null)
        assertTrue(roots.size >= 5)
        assertTrue(roots.any { it.name == "计算机" })
    }

    @Test @Order(6)
    fun `category selectByParentId returns children`() {
        val children = categoryMapper.selectByParentId("CAT-01")
        assertTrue(children.size >= 3)
        assertTrue(children.any { it.name == "程序设计" })
    }

    @Test @Order(7)
    fun `category insert and delete`() {
        val cat = BookCategory("CAT-TEST", "测试分类", "CAT-01")
        categoryMapper.insert(cat); session.commit()

        var found = categoryMapper.selectById("CAT-TEST")
        assertNotNull(found)
        assertEquals("测试分类", found!!.name)

        categoryMapper.delete("CAT-TEST"); session.commit()
        found = categoryMapper.selectById("CAT-TEST")
        assertNull(found)
    }

    // ==================== BookstoreInfo ====================

    @Test @Order(8)
    fun `bookstoreInfo selectByName returns default`() {
        val info = bookstoreInfoMapper.selectByName("内电子信息学院书店")
        assertNotNull(info)
        assertEquals("校园书店，服务师生", info!!.description)
    }

    // ==================== Book ====================

    @Test @Order(9)
    fun `book insert search update delete`() {
        val book = Book("BK-001", "CAT-01-01", "Kotlin入门", null, "王五", "清华大学出版社", "1", "978-7-302-00001", 59.0, 10, "一本好书", "Kotlin Java", "2024-01", null)
        bookMapper.insert(book); session.commit()

        var found = bookMapper.selectById("BK-001")
        assertNotNull(found)
        assertEquals("Kotlin入门", found!!.title)

        val results = bookMapper.searchByKeyword("Kotlin")
        assertTrue(results.any { it.bookId == "BK-001" })

        bookMapper.updateStock("BK-001", 20); session.commit()
        found = bookMapper.selectById("BK-001")
        assertEquals(20, found!!.stock)

        bookMapper.delete("BK-001"); session.commit()
        found = bookMapper.selectById("BK-001")
        assertNull(found)
    }

    // ==================== BookPrice ====================

    @Test @Order(10)
    fun `bookPrice insert update delete`() {
        val bp = BookPrice("BK-001", 35.0, "2024-01-15")
        bookPriceMapper.insert(bp); session.commit()

        var found = bookPriceMapper.selectById("BK-001")
        assertNotNull(found)
        assertEquals(35.0, found!!.price)

        bookPriceMapper.update(bp.copy(price = 32.0)); session.commit()
        found = bookPriceMapper.selectById("BK-001")
        assertEquals(32.0, found!!.price)

        bookPriceMapper.delete("BK-001"); session.commit()
        found = bookPriceMapper.selectById("BK-001")
        assertNull(found)
    }

    // ==================== PurchaseRecord ====================

    @Test @Order(11)
    fun `purchaseRecord insert and query`() {
        val record = PurchaseRecord(0, "SUP-001", "BK-001", 100, 35.0, 0.9, 3150.0, "2024-06-01", "首批进货")
        purchaseRecordMapper.insert(record); session.commit()
        assertTrue(record.id > 0)

        var found = purchaseRecordMapper.selectById(record.id)
        assertNotNull(found)
        assertEquals(100, found!!.quantity)

        val bySupplier = purchaseRecordMapper.selectBySupplier("SUP-001")
        assertTrue(bySupplier.isNotEmpty())

        purchaseRecordMapper.delete(record.id); session.commit()
        found = purchaseRecordMapper.selectById(record.id)
        assertNull(found)
    }

    // ==================== ReturnRecord ====================

    @Test @Order(12)
    fun `returnRecord insert and query`() {
        val record = ReturnRecord(0, "SUP-001", "BK-001", 35.0, 10, 350.0, "印刷质量问题", "2024-06-10", null)
        returnRecordMapper.insert(record); session.commit()
        assertTrue(record.id > 0)

        var found = returnRecordMapper.selectById(record.id)
        assertNotNull(found)
        assertEquals("印刷质量问题", found!!.reason)

        returnRecordMapper.delete(record.id); session.commit()
        found = returnRecordMapper.selectById(record.id)
        assertNull(found)
    }

    // ==================== SaleRecord ====================

    @Test @Order(13)
    fun `saleRecord insert and query`() {
        val record = SaleRecord(0, "BK-001", 2, null, 1.0, 118.0, "2024-06-15", null)
        saleRecordMapper.insert(record); session.commit()
        assertTrue(record.id > 0)

        var found = saleRecordMapper.selectById(record.id)
        assertNotNull(found)
        assertEquals(2, found!!.quantity)

        val byDate = saleRecordMapper.selectByDate("2024-06-15")
        assertTrue(byDate.isNotEmpty())

        saleRecordMapper.delete(record.id); session.commit()
        found = saleRecordMapper.selectById(record.id)
        assertNull(found)
    }

    // ==================== Member ====================

    @Test @Order(14)
    fun `member insert update delete`() {
        val member = Member("M-001", 1, "赵六", "女", "上海", "XX大学", "021-000", "zhao@test.com", "学无止境", "2024-06-01")
        memberMapper.insert(member); session.commit()

        var found = memberMapper.selectById("M-001")
        assertNotNull(found)
        assertEquals("赵六", found!!.name)

        memberMapper.updateLevel("M-001", 2); session.commit()
        found = memberMapper.selectById("M-001")
        assertEquals(2, found!!.level)

        memberMapper.delete("M-001"); session.commit()
        found = memberMapper.selectById("M-001")
        assertNull(found)
    }

    // ==================== MemberPolicy ====================

    @Test @Order(15)
    fun `memberPolicy selectByMinAmount returns correct level`() {
        var policy = memberPolicyMapper.selectByMinAmount(800)
        assertNotNull(policy)
        assertEquals(2, policy!!.level) // 800元：≥500 → 等级2

        policy = memberPolicyMapper.selectByMinAmount(10000)
        assertNotNull(policy)
        assertEquals(5, policy!!.level)
    }

    // ==================== Feedback ====================

    @Test @Order(16)
    fun `feedback insert and select`() {
        val fb = Feedback(0, "钱七", "会员", "男", null, null, "q@test.com", "服务很好！", "2024-06-20")
        feedbackMapper.insert(fb); session.commit()
        assertTrue(fb.id > 0)

        var found = feedbackMapper.selectById(fb.id)
        assertNotNull(found)
        assertEquals("服务很好！", found!!.content)

        feedbackMapper.delete(fb.id); session.commit()
        found = feedbackMapper.selectById(fb.id)
        assertNull(found)
    }
}
