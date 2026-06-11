package org.zl.team.ui.sale

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.service.SaleService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodayStatScreen() {
    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    var todaySales by remember { mutableStateOf(emptyList<org.zl.team.entity.SaleRecord>()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            todaySales = SaleService.listByDateRange(today, today)
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    when {
        isLoading -> LoadingIndicator()
        loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
        todaySales.isEmpty() && !isLoading && loadError == null -> EmptyHint("今日暂无销售记录")
        else -> {
            val totalQty = todaySales.sumOf { it.quantity ?: 0 }
            val totalAmount = todaySales.sumOf { it.amount ?: 0.0 }
            val totalDiscount = todaySales.sumOf { (1.0 - it.discount) * (it.amount ?: 0.0) / (it.discount.coerceAtLeast(0.01)) }

            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Text("今日统计", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard("销售笔数", "${todaySales.size}", MaterialTheme.colorScheme.primary)
                    StatCard("销售数量", "$totalQty", MaterialTheme.colorScheme.tertiary)
                    StatCard("销售金额", "¥%.2f".format(totalAmount), MaterialTheme.colorScheme.secondary)
                    StatCard("优惠总额", "¥%.2f".format(totalDiscount), MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(32.dp))

                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(24.dp)) {
                        Text("日期: $today", fontWeight = FontWeight.Medium, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.height(16.dp))
                        todaySales.take(20).forEach { r ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(r.bookId ?: "-", fontSize = 13.sp, modifier = Modifier.weight(1f))
                                Text("x${r.quantity}", fontSize = 13.sp, modifier = Modifier.width(60.dp))
                                Text("¥%.2f".format(r.amount ?: 0.0), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(80.dp))
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.StatCard(title: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Surface(shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.1f), modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
