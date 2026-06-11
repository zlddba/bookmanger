package org.zl.team.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.OperationLog
import org.zl.team.service.*
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen() {
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var todaySalesCount by remember { mutableStateOf(0) }
    var todaySalesAmount by remember { mutableStateOf(0.0) }
    var lowStockCount by remember { mutableStateOf(0) }
    var overdueCount by remember { mutableStateOf(0) }
    var lowStockBooks by remember { mutableStateOf<List<StockAlertService.StockAlert>>(emptyList()) }
    var recentLogs by remember { mutableStateOf<List<OperationLog>>(emptyList()) }
    var memberCount by remember { mutableStateOf(0) }
    var topMembers by remember { mutableStateOf<List<org.zl.team.entity.Member>>(emptyList()) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val sales = SaleService.listByDate(today)
            todaySalesCount = sales.size
            todaySalesAmount = sales.sumOf { it.amount ?: 0.0 }

            lowStockBooks = StockAlertService.getLowStockBooks(10)
            lowStockCount = lowStockBooks.size
            overdueCount = BorrowService.getOverdueCount()
            recentLogs = OperationLogService.listRecent(20)
            val allMembers = MemberService.listAll()
            memberCount = allMembers.size
            topMembers = allMembers.sortedByDescending { it.totalSpent }.take(5)
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("工作台", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> LoadingIndicator()
            loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
            else -> {
                // ─── 统计卡片 ──────────────────────────────
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DashCard("今日销售", "$todaySalesCount 笔", "¥%.2f".format(todaySalesAmount), MaterialTheme.colorScheme.primary)
                    DashCard("库存预警", "$lowStockCount 种", "库存不足", if (lowStockCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                    DashCard("逾期借阅", "$overdueCount 笔", "需催还", if (overdueCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                    DashCard("会员总数", "$memberCount 人", "消费排行", MaterialTheme.colorScheme.tertiary)
                }

                Spacer(Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // ─── 库存预警列表 ───────────────────────
                    Surface(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        Column(Modifier.fillMaxSize().padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("库存预警", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                if (lowStockCount > 0) {
                                    Spacer(Modifier.width(8.dp))
                                    Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.error) {
                                        Text("$lowStockCount", modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp), fontSize = 10.sp, color = MaterialTheme.colorScheme.onError)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            if (lowStockBooks.isEmpty()) {
                                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Text("暂无库存预警", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                                }
                            } else {
                                LazyColumn(Modifier.weight(1f)) {
                                    itemsIndexed(lowStockBooks.take(15)) { _, alert ->
                                        Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Column(Modifier.weight(1f)) {
                                                Text(alert.book.title, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                Text(alert.book.bookId, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text("库存: ${alert.book.stock}", fontSize = 13.sp,
                                                color = if (alert.book.stock <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Medium)
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                                    }
                                }
                            }
                        }
                    }

                    // ─── 右侧：会员排行 + 操作日志 ─────────
                    Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // ─── 会员消费排行 ───────────────────
                        Surface(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp
                        ) {
                            Column(Modifier.fillMaxSize().padding(16.dp)) {
                                Text("会员消费排行 Top 5", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Spacer(Modifier.height(8.dp))
                                if (topMembers.isEmpty()) {
                                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Text("暂无会员数据", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                                    }
                                } else {
                                    LazyColumn(Modifier.weight(1f)) {
                                        itemsIndexed(topMembers) { i, m ->
                                            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Text("#${i + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                                    color = when (i) { 0 -> Color(0xFFFFD700); 1 -> Color(0xFFC0C0C0); 2 -> Color(0xFFCD7F32); else -> MaterialTheme.colorScheme.onSurfaceVariant })
                                                Spacer(Modifier.width(8.dp))
                                                Column(Modifier.weight(1f)) {
                                                    Text(m.name, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Text("等级${m.level} | ${m.cardNo}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                                Text("¥%.2f".format(m.totalSpent), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                                            }
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                                        }
                                    }
                                }
                            }
                        }

                        // ─── 最近操作日志 ───────────────────
                        Surface(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp
                        ) {
                            Column(Modifier.fillMaxSize().padding(16.dp)) {
                                Text("最近操作", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Spacer(Modifier.height(8.dp))
                                if (recentLogs.isEmpty()) {
                                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                        Text("暂无操作记录", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                                    }
                                } else {
                                    LazyColumn(Modifier.weight(1f)) {
                                        itemsIndexed(recentLogs) { _, log ->
                                            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                                val actionColor = when (log.action) {
                                                    "删除" -> MaterialTheme.colorScheme.error
                                                    "新增" -> MaterialTheme.colorScheme.primary
                                                    "修改" -> MaterialTheme.colorScheme.tertiary
                                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                                Surface(shape = RoundedCornerShape(4.dp), color = actionColor.copy(alpha = 0.12f)) {
                                                    Text(log.action, modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp), fontSize = 10.sp, color = actionColor, fontWeight = FontWeight.Medium)
                                                }
                                                Spacer(Modifier.width(6.dp))
                                                Column(Modifier.weight(1f)) {
                                                    Text(log.detail ?: "${log.target} ${log.targetId ?: ""}", fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Row {
                                                        Text(log.operator, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        val timeStr = log.createdAt?.takeLast(8)
                                                        if (timeStr != null) {
                                                            Text(" | $timeStr", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        }
                                                    }
                                                }
                                            }
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DashCard(title: String, subtitle: String, value: String, color: Color) {
    Surface(shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.08f), modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(20.dp)) {
            Text(title, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}
