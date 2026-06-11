package org.zl.team.ui.finance

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.service.GrossProfitService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.DateField

@Composable
fun GrossProfitScreen() {
    var rows by remember { mutableStateOf(emptyList<GrossProfitService.GrossProfitRow>()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var startDate by remember { mutableStateOf(LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var endDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }

    fun query() {
        isLoading = true
        loadError = null
        try {
            rows = GrossProfitService.query(startDate, endDate)
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { query() }

    val totalSale = rows.sumOf { it.saleAmount }
    val totalCost = rows.sumOf { it.cost }
    val totalProfit = rows.sumOf { it.grossProfit }
    val profitRate = if (totalSale > 0.0) totalProfit / totalSale * 100 else 0.0

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("毛利统计", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
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
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("销售收入", "¥%.2f".format(totalSale), MaterialTheme.colorScheme.primary)
            StatCard("销售成本", "¥%.2f".format(totalCost), MaterialTheme.colorScheme.error)
            StatCard("毛利", "¥%.2f".format(totalProfit), if (totalProfit >= 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)
            StatCard("毛利率", "%.1f%%".format(profitRate), MaterialTheme.colorScheme.tertiary)
        }
        Spacer(Modifier.height(16.dp))
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column {
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 12.dp, vertical = 12.dp)) {
                    Text("日期", Modifier.weight(0.7f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("图书", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("数量", Modifier.weight(0.4f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("售价", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("进价", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("成本", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("毛利", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                when {
                    isLoading -> LoadingIndicator()
                    loadError != null -> ErrorBanner(loadError!!, onRetry = ::query)
                    rows.isEmpty() -> EmptyHint("暂无数据")
                    else -> {
                        LazyColumn {
                            itemsIndexed(rows) { index, row ->
                                Row(modifier = Modifier.fillMaxWidth().background(
                                    if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                ).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(row.date, Modifier.weight(0.7f), fontSize = 13.sp)
                                    Text(row.bookTitle, Modifier.weight(1.2f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${row.quantity}", Modifier.weight(0.4f), fontSize = 13.sp)
                                    Text("¥%.2f".format(row.unitPrice), Modifier.weight(0.6f), fontSize = 13.sp)
                                    Text("¥%.2f".format(row.costPrice), Modifier.weight(0.6f), fontSize = 13.sp)
                                    Text("¥%.2f".format(row.cost), Modifier.weight(0.6f), fontSize = 13.sp)
                                    Text("¥%.2f".format(row.grossProfit), Modifier.weight(0.6f), fontSize = 13.sp,
                                        color = if (row.grossProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium)
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
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
