package org.zl.team

import org.zl.team.config.DatabaseInitializer
import org.zl.team.entity.Feedback
import org.zl.team.entity.Member
import org.zl.team.service.FeedbackService
import org.zl.team.service.MemberService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Paths
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Phase9ServiceTest {

    private val testDbPath = "test_phase9.db"

    @BeforeAll
    fun setUp() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
        DatabaseInitializer.initForTest(testDbPath)
    }

    @AfterAll
    fun tearDown() {
        try { Files.deleteIfExists(Paths.get(testDbPath)) } catch (_: Exception) { }
    }

    // ========== FeedbackService 测试 ==========

    @Test
    fun `submit feedback succeeds`() {
        val fb = Feedback(0, "张三", "会员", "男", "清华", "北京", "zhang@test.com", "系统很好用", "2024-01-15")
        assertTrue(FeedbackService.submit(fb))

        val all = FeedbackService.listAll()
        assertEquals(1, all.size)
        assertEquals("张三", all[0].name)
        assertEquals("系统很好用", all[0].content)
    }

    @Test
    fun `listAll returns all feedback`() {
        FeedbackService.submit(Feedback(0, "李四", "游客", null, null, null, "li@test.com", "建议增加搜索功能", "2024-01-16"))
        FeedbackService.submit(Feedback(0, "王五", "会员", "女", "北大", null, "wang@test.com", "界面友好", "2024-01-17"))
        val all = FeedbackService.listAll()
        assertTrue(all.size >= 2)
        val names = all.map { it.name }
        assertTrue(names.contains("李四"))
        assertTrue(names.contains("王五"))
    }

    @Test
    fun `submit feedback with minimal fields`() {
        val fb = Feedback(0, "匿名", null, null, null, null, null, "测试反馈", null)
        assertTrue(FeedbackService.submit(fb))
    }

    // ========== MemberService 会员自助测试 ==========

    @Test
    fun `member self update profile`() {
        MemberService.create(Member("SELF01", 1, "旧名", "男", "旧地址", null, null, null, null, "2024-01-01"), "123456")
        val updated = Member("SELF01", 1, "新名", "女", "新地址", "新单位", "010-88888888", "self@test.com", "新格言", null)
        assertTrue(MemberService.update(updated))

        val found = MemberService.getById("SELF01")
        assertEquals("新名", found!!.name)
        assertEquals("女", found.gender)
        assertEquals("新地址", found.address)
        assertEquals("新单位", found.company)
        assertEquals("010-88888888", found.phone)
        assertEquals("self@test.com", found.email)
        assertEquals("新格言", found.motto)
    }

    @Test
    fun `member change own password`() {
        MemberService.create(Member("SELF02", 1, "密码会员", null, null, null, null, null, null, null), "oldpwd")
        assertTrue(MemberService.changePassword("SELF02", "oldpwd", "newpwd"))
        // 验证旧密码失效
        assertFalse(MemberService.changePassword("SELF02", "oldpwd", "another"))
    }
}
