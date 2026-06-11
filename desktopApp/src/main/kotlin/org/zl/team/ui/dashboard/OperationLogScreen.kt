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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.OperationLog
import org.zl.team.service.OperationLogService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun OperationLogScreen() {
    var logs by remember { mutableStateOf<List<OperationLog>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var actionFilter by remember { mutableStateOf("") }

    fun load() {
        isLoading = true
        loadError = null
        try {
            logs = OperationLogService.listAll()
            if (actionFilter.isNotBlank()) {
                logs = logs.filter { it.action == actionFilter }
            }
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("操作日志", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.weight(1f))
            var filterExpanded by remember { mutableStateOf(false) }
            Box {
                FilterChip(selected = actionFilter.isNotBlank(), onClick = { filterExpanded = true },
                    label = { Text(if (actionFilter.isNotBlank()) "操作: $actionFilter" else "全部操作", fontSize = 13.sp) })
                DropdownMenu(expanded = filterExpanded, onDismissRequest = { filterExpanded = false }) {
                    DropdownMenuItem(text = { Text("全部") }, onClick = { actionFilter = ""; filterExpanded = false; load() })
                    listOf("新增", "修改", "删除", "销售", "进货", "借阅", "归还", "备份", "恢复", "导入").forEach { a ->
                        DropdownMenuItem(text = { Text(a) }, onClick = { actionFilter = a; filterExpanded = false; load() })
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
            when {
                isLoading -> LoadingIndicator()
                loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
                logs.isEmpty() -> EmptyHint("暂无操作记录")
                else -> {
                    Column {
                        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("操作", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("对象", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("详情", Modifier.weight(1.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("操作人", Modifier.weight(0.7f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("时间", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        LazyColumn {
                            itemsIndexed(logs) { index, log ->
                                val actionColor = when (log.action) {
                                    "删除" -> MaterialTheme.colorScheme.error
                                    "新增" -> MaterialTheme.colorScheme.primary
                                    "修改" -> MaterialTheme.colorScheme.tertiary
                                    "销售" -> MaterialTheme.colorScheme.primary
                                    "进货" -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                                Row(modifier = Modifier.fillMaxWidth().background(
                                    if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                ).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = actionColor.copy(alpha = 0.12f)) {
                                        Text(log.action, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 11.sp, color = actionColor, fontWeight = FontWeight.Medium)
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    Text(log.target, Modifier.weight(0.6f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(log.detail ?: "-", Modifier.weight(1.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(log.operator, Modifier.weight(0.7f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(log.createdAt?.takeLast(8) ?: "-", Modifier.weight(1f), fontSize = 13.sp)
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
