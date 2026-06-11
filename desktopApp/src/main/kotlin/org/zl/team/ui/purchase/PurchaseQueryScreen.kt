package org.zl.team.ui.purchase

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import org.zl.team.service.PurchaseService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.DateField
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PurchaseQueryScreen() {
    var records by remember { mutableStateOf(emptyList<org.zl.team.entity.PurchaseRecord>()) }
    var startDate by remember { mutableStateOf(LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var endDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var expandedId by remember { mutableStateOf<Int?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun query() {
        isLoading = true
        loadError = null
        try {
            records = PurchaseService.listByDateRange(startDate, endDate)
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
        records.isEmpty() && !isLoading && loadError == null -> EmptyHint("暂无进货记录")
        else -> {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Text("进货查询", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("日期范围: ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    DateField(value = startDate, onValueChange = { startDate = it }, label = "开始日期", modifier = Modifier.width(140.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("~", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    DateField(value = endDate, onValueChange = { endDate = it }, label = "结束日期", modifier = Modifier.width(140.dp))
                    Spacer(Modifier.width(12.dp))
                    Button(onClick = { query() }, shape = RoundedCornerShape(10.dp)) { Text("查询") }
                    Spacer(Modifier.width(8.dp))
                    Text("共 ${records.size} 条记录", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(16.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column {
                        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("日期", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("图书编号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("供应商", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("数量", Modifier.weight(0.4f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("单价", Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("折扣", Modifier.weight(0.4f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("金额", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        LazyColumn {
                            itemsIndexed(records) { index, r ->
                                val isExpanded = expandedId == r.id
                                Column(modifier = Modifier.fillMaxWidth().background(
                                    if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                )) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { expandedId = if (isExpanded) null else r.id }.padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(r.date ?: "-", Modifier.weight(0.8f), fontSize = 13.sp)
                                        Text(r.bookId ?: "-", Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(r.supplierId ?: "-", Modifier.weight(0.6f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${r.quantity}", Modifier.weight(0.4f), fontSize = 13.sp)
                                        Text(if (r.unitPrice != null) "¥%.2f".format(r.unitPrice) else "-", Modifier.weight(0.5f), fontSize = 13.sp)
                                        Text("${r.discount}", Modifier.weight(0.4f), fontSize = 13.sp)
                                        Text(if (r.amount != null) "¥%.2f".format(r.amount) else "-", Modifier.weight(0.6f), fontSize = 13.sp)
                                    }
                                    if (isExpanded && !r.remark.isNullOrBlank()) {
                                        Text("备注: ${r.remark}", modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
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
