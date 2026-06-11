package org.zl.team.ui.member

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.BorrowRecord
import org.zl.team.service.BookService
import org.zl.team.service.BorrowService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.StatusChip
import org.zl.team.util.SessionManager

@Composable
fun MemberBorrowHistoryScreen() {
    val cardNo = SessionManager.currentUserId
    var records by remember { mutableStateOf<List<BorrowRecord>>(emptyList()) }
    var bookTitles by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            val list = BorrowService.listByMember(cardNo)
            val titles = list.map { it.bookId }.distinct().associateWith { bid ->
                BookService.getById(bid)?.title ?: bid
            }
            records = list
            bookTitles = titles
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("我的借阅记录", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> LoadingIndicator()
            loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
            records.isEmpty() -> EmptyHint("暂无借阅记录")
            else -> {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                    Column(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("编号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("书名", Modifier.weight(1.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("借书日期", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("应还日期", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("归还日期", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("状态", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("续借", Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        records.forEachIndexed { index, r ->
                            val isOverdue = r.status == "借阅中" && r.dueDate < java.time.LocalDate.now().toString()
                            Row(Modifier.fillMaxWidth().background(
                                if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(r.bookId, Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(bookTitles[r.bookId] ?: r.bookId, Modifier.weight(1.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(r.borrowDate, Modifier.weight(1.2f), fontSize = 13.sp)
                                Text(r.dueDate, Modifier.weight(1.2f), fontSize = 13.sp)
                                Text(r.returnDate ?: "-", Modifier.weight(1.2f), fontSize = 13.sp)
                                StatusChip(if (isOverdue) "已逾期" else r.status, Modifier.weight(0.8f))
                                Text("${r.renewCount}", Modifier.weight(0.5f), fontSize = 13.sp)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                        }
                    }
                }
            }
        }
    }
}
