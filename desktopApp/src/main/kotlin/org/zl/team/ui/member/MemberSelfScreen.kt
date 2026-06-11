package org.zl.team.ui.member

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.BorrowRecord
import org.zl.team.entity.Member
import org.zl.team.service.BookService
import org.zl.team.service.BorrowService
import org.zl.team.service.MemberService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.StatusChip
import org.zl.team.util.SessionManager
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun MemberSelfScreen() {
    val currentUser = SessionManager.currentUserId
    var member by remember { mutableStateOf<Member?>(null) }
    var borrows by remember { mutableStateOf<List<BorrowRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            member = MemberService.getById(currentUser)
            borrows = BorrowService.listByMember(currentUser)
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(currentUser) { load() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        when {
            isLoading -> LoadingIndicator()
            loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
            member == null -> EmptyHint("未找到会员信息")
            else -> {
                val m = member!!

                // ─── 会员概览卡片 ───────────────────────────
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(64.dp)) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(m.name.take(1), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(m.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text("会员卡号: ${m.cardNo}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Row {
                                Text("Lv.${m.level}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.width(12.dp))
                                Text("累计消费 ¥%.2f".format(m.totalSpent), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ─── 汇总统计 ───────────────────────────────
                val today = LocalDate.now().toString()
                val activeBorrows = borrows.filter { it.status == "借阅中" && it.dueDate >= today }
                val overdueBorrows = borrows.filter { it.status == "借阅中" && it.dueDate < today }
                val activeCount = activeBorrows.size
                val overdueCount = overdueBorrows.size

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("当前借阅", "$activeCount 本", MaterialTheme.colorScheme.primary)
                    StatCard("逾期", "$overdueCount 本", if (overdueCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                    StatCard("累计借阅", "${borrows.size} 次", MaterialTheme.colorScheme.tertiary)
                }

                Spacer(Modifier.height(16.dp))

                // ─── Tab 切换 ───────────────────────────────
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("个人信息") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("借阅记录") })
                }

                Spacer(Modifier.height(12.dp))

                // ─── Tab 内容 ───────────────────────────────
                when (selectedTab) {
                    0 -> ProfileTab(m)
                    1 -> BorrowHistoryTab(borrows, onRenew = { id -> BorrowService.renew(id); load() })
                }

                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun ProfileTab(member: Member) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
            SectionTitle("基本信息")
            Spacer(Modifier.height(8.dp))
            InfoRow("姓名", member.name)
            InfoRow("性别", member.gender)
            InfoRow("卡号", member.cardNo)
            InfoRow("会员等级", "Lv.${member.level}")
            InfoRow("累计消费", "¥%.2f".format(member.totalSpent))

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(16.dp))
            SectionTitle("联系方式")
            Spacer(Modifier.height(8.dp))
            InfoRow("电话", member.phone)
            InfoRow("邮箱", member.email)
            InfoRow("地址", member.address)
            InfoRow("单位", member.company)
            InfoRow("格言", member.motto)
            InfoRow("注册日期", member.regDate)
        }
    }
}

@Composable
private fun BorrowHistoryTab(records: List<BorrowRecord>, onRenew: (Int) -> Unit) {
    if (records.isEmpty()) {
        Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
            Text("暂无借阅记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
        Column(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 12.dp, vertical = 12.dp)) {
                Text("书名", Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("借阅日", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("应还日", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("状态", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("操作", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                itemsIndexed(records) { index, r ->
                    val today = LocalDate.now()
                    val isOverdue = r.status != "已归还" && r.dueDate < today.toString()
                    val overdueDays = if (isOverdue) ChronoUnit.DAYS.between(LocalDate.parse(r.dueDate), today) else 0

                    Row(Modifier.fillMaxWidth().background(
                        if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            BookService.getById(r.bookId)?.title ?: r.bookId,
                            Modifier.weight(2f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        Text(r.borrowDate, Modifier.weight(1f), fontSize = 12.sp)
                        Text(r.dueDate, Modifier.weight(1f), fontSize = 12.sp,
                            color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                        StatusChip(if (isOverdue) "已逾期 (${overdueDays}天)" else r.status, Modifier.weight(1f))
                        Row(Modifier.weight(1f)) {
                            if (!isOverdue && r.status == "借阅中") {
                                TextButton(onClick = { onRenew(r.id) }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                                    Text("续借", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (value != null && value.isNotBlank()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
            Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(72.dp))
            Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun RowScope.StatCard(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Surface(shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.08f), modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
