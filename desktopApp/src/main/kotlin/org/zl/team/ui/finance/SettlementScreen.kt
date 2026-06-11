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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.service.SettlementService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.DateField

@Composable
fun SettlementScreen() {
    var mode by remember { mutableStateOf("日结") }
    var rows by remember { mutableStateOf(emptyList<SettlementService.SettlementRow>()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var startDate by remember { mutableStateOf(LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var endDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }

    fun query() {
        isLoading = true
        loadError = null
        try {
            rows = if (mode == "日结") SettlementService.dailySettlement(startDate, endDate)
            else SettlementService.monthlySettlement(startDate, endDate)
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { query() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("结算对账", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("日期: ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            DateField(value = startDate, onValueChange = { startDate = it }, label = "开始日期", modifier = Modifier.width(140.dp))
            Spacer(Modifier.width(8.dp)); Text("~", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(8.dp))
            DateField(value = endDate, onValueChange = { endDate = it }, label = "结束日期", modifier = Modifier.width(140.dp))
            Spacer(Modifier.width(12.dp))
            Button(onClick = { query() }, shape = RoundedCornerShape(10.dp)) { Text("查询") }
            Spacer(Modifier.width(12.dp))
            var modeExpanded by remember { mutableStateOf(false) }
            Box {
                FilterChip(selected = true, onClick = { modeExpanded = true }, label = { Text(mode, fontSize = 13.sp) })
                DropdownMenu(expanded = modeExpanded, onDismissRequest = { modeExpanded = false }) {
                    DropdownMenuItem(text = { Text("日结") }, onClick = { mode = "日结"; modeExpanded = false })
                    DropdownMenuItem(text = { Text("月结") }, onClick = { mode = "月结"; modeExpanded = false })
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        val totals = rows.let { list ->
            SettlementService.SettlementRow(
                "合计", list.sumOf { it.saleAmount }, list.sumOf { it.saleCount },
                list.sumOf { it.purchaseAmount }, list.sumOf { it.purchaseCount },
                list.sumOf { it.returnAmount }, list.sumOf { it.returnCount },
                list.sumOf { it.profit }
            )
        }

        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column {
                Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 12.dp, vertical = 12.dp)) {
                    Text("期间", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("销售", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("销售笔数", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("进货", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("进货笔数", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("退货", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Text("利润", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
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
                                    Text(row.period, Modifier.weight(0.8f), fontSize = 13.sp)
                                    Text("¥%.2f".format(row.saleAmount), Modifier.weight(0.8f), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                    Text("${row.saleCount}", Modifier.weight(0.6f), fontSize = 13.sp)
                                    Text("¥%.2f".format(row.purchaseAmount), Modifier.weight(0.8f), fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                                    Text("${row.purchaseCount}", Modifier.weight(0.6f), fontSize = 13.sp)
                                    Text("¥%.2f".format(row.returnAmount), Modifier.weight(0.8f), fontSize = 13.sp)
                                    Text("¥%.2f".format(row.profit), Modifier.weight(0.8f), fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                        color = if (row.profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text("合计: ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("销售 ¥${"%.2f".format(totals.saleAmount)}(${totals.saleCount}笔)  ", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Text("进货 ¥${"%.2f".format(totals.purchaseAmount)}(${totals.purchaseCount}笔)  ", fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                Text("利润 ¥${"%.2f".format(totals.profit)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (totals.profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
        }
    }
}
