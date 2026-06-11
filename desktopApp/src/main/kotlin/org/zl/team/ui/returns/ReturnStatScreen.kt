package org.zl.team.ui.returns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.service.ReturnService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.DateField
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReturnStatScreen() {
    var records by remember { mutableStateOf(emptyList<org.zl.team.entity.ReturnRecord>()) }
    var startDate by remember { mutableStateOf(LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var endDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun query() {
        isLoading = true
        loadError = null
        try {
            records = ReturnService.listByDateRange(startDate, endDate)
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { query() }

    when {
        isLoading -> LoadingIndicator()
        loadError != null -> ErrorBanner(loadError!!, onRetry = ::query)
        records.isEmpty() && !isLoading && loadError == null -> EmptyHint("暂无退货统计数据")
        else -> {
            val totalQty = records.sumOf { it.quantity ?: 0 }
            val totalAmount = records.sumOf { it.amount ?: 0.0 }
            val groupByBook = records.groupBy { it.bookId ?: "未知" }
                .mapValues { (_, list) -> list.sumOf { it.quantity ?: 0 } }
                .entries.sortedByDescending { it.value }

            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Text("退货统计", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("日期范围: ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    DateField(value = startDate, onValueChange = { startDate = it }, label = "开始日期", modifier = Modifier.width(140.dp))
                    Spacer(Modifier.width(8.dp)); Text("~", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    DateField(value = endDate, onValueChange = { endDate = it }, label = "结束日期", modifier = Modifier.width(140.dp))
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = { query() }, shape = RoundedCornerShape(10.dp)) { Text("查询") }
                }
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard("退货总次数", "${records.size}", MaterialTheme.colorScheme.primary)
                    StatCard("退货总数量", "$totalQty", MaterialTheme.colorScheme.tertiary)
                    StatCard("退货总金额", "¥%.2f".format(totalAmount), MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(16.dp))

                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column {
                        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("图书", Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("退货数量", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        LazyColumn {
                            itemsIndexed(groupByBook) { index, (bookId, qty) ->
                                Row(modifier = Modifier.fillMaxWidth().background(
                                    if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                ).padding(horizontal = 16.dp, vertical = 10.dp)) {
                                    Text(bookId, Modifier.weight(2f), fontSize = 13.sp)
                                    Text("$qty", Modifier.weight(0.8f), fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                            }
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
