package org.zl.team.ui.feedback

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
import org.zl.team.entity.Feedback
import org.zl.team.service.FeedbackService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun FeedbackManageScreen() {
    var feedbacks by remember { mutableStateOf<List<Feedback>>(emptyList()) }
    var expandedId by remember { mutableStateOf<Int?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            feedbacks = FeedbackService.listAll()
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
        feedbacks.isEmpty() && !isLoading && loadError == null -> EmptyHint("暂无反馈数据")
        else -> {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Text("反馈管理", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(16.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column {
                        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("日期", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("姓名", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("身份", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("内容", Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        LazyColumn {
                            itemsIndexed(feedbacks) { index, f ->
                                val isExpanded = expandedId == f.id
                                Column(modifier = Modifier.fillMaxWidth().background(
                                    if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                )) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { expandedId = if (isExpanded) null else f.id }.padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(f.date ?: "-", Modifier.weight(0.8f), fontSize = 13.sp)
                                        Text(f.name ?: "-", Modifier.weight(0.6f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(f.role ?: "-", Modifier.weight(0.6f), fontSize = 13.sp, maxLines = 1)
                                        Text(f.content ?: "", Modifier.weight(2f), fontSize = 13.sp, maxLines = if (isExpanded) Int.MAX_VALUE else 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    if (isExpanded) {
                                        Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(6.dp)) {
                                            Column(Modifier.padding(12.dp)) {
                                                Text("邮箱: ${f.email ?: "无"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text("单位: ${f.company ?: "无"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text("地址: ${f.address ?: "无"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        }
    }
}
