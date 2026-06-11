package org.zl.team.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.Book
import org.zl.team.entity.BorrowRecord
import org.zl.team.entity.Member
import org.zl.team.service.BookService
import org.zl.team.service.BorrowService
import org.zl.team.service.BorrowService.MAX_BORROW
import org.zl.team.service.MemberService
import org.zl.team.service.SystemConfig
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.DateField
import org.zl.team.util.CsvExporter

@Composable
fun BorrowManageScreen() {
    var records by remember { mutableStateOf<List<BorrowRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var statusFilter by remember { mutableStateOf("") }

    fun load() {
        isLoading = true
        loadError = null
        try {
            records = if (statusFilter.isBlank()) BorrowService.listAll() else BorrowService.listByStatus(statusFilter)
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("借阅管理", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.weight(1f))
                var filterExpanded by remember { mutableStateOf(false) }
                Box {
                    FilterChip(selected = statusFilter.isNotBlank(), onClick = { filterExpanded = true }, label = { Text(if (statusFilter.isNotBlank()) statusFilter else "全部", fontSize = 13.sp) })
                    DropdownMenu(expanded = filterExpanded, onDismissRequest = { filterExpanded = false }) {
                        DropdownMenuItem(text = { Text("全部") }, onClick = { statusFilter = ""; filterExpanded = false; load() })
                        DropdownMenuItem(text = { Text("借阅中") }, onClick = { statusFilter = "借阅中"; filterExpanded = false; load() })
                        DropdownMenuItem(text = { Text("已归还") }, onClick = { statusFilter = "已归还"; filterExpanded = false; load() })
                        DropdownMenuItem(text = { Text("已逾期") }, onClick = { statusFilter = "已逾期"; filterExpanded = false; load() })
                    }
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { showDialog = true }, shape = RoundedCornerShape(10.dp)) { Text("新增借阅") }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = {
                    CsvExporter.export(
                        listOf("图书", "会员卡", "借阅日", "到期日", "状态", "滞纳金"),
                        records.map { r ->
                            val today = LocalDate.now()
                            val isOver = r.status == "借阅中" && try { LocalDate.parse(r.dueDate).isBefore(today) } catch (_: Exception) { false }
                            val days = if (isOver) ChronoUnit.DAYS.between(try { LocalDate.parse(r.dueDate) } catch (_: Exception) { today }, today) else 0
                            val fee = days * SystemConfig.getLateFeeDailyRate()
                            listOf(r.bookId, r.cardNo, r.borrowDate, r.dueDate,
                                if (isOver) "已逾期" else r.status,
                                if (isOver) "¥%.2f".format(fee) else "-")
                        },
                        "借阅记录.csv"
                    )
                }, shape = RoundedCornerShape(10.dp)) { Text("导出 CSV") }
            }
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column {
                    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text("图书", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("会员卡", Modifier.weight(0.7f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("借阅日", Modifier.weight(0.7f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("到期日", Modifier.weight(0.7f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("逾期", Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("滞纳金", Modifier.weight(0.7f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("操作", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    when {
                        isLoading -> LoadingIndicator()
                        loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
                        records.isEmpty() -> EmptyHint("暂无数据")
                        else -> {
                            LazyColumn {
                                itemsIndexed(records) { index, r ->
                                    val today = LocalDate.now()
                                    val isOverdue = r.status == "借阅中" && try { LocalDate.parse(r.dueDate).isBefore(today) } catch (_: Exception) { false }
                                    val overdueDays = if (isOverdue) ChronoUnit.DAYS.between(try { LocalDate.parse(r.dueDate) } catch (_: Exception) { today }, today) else 0
                                    val fine = overdueDays * SystemConfig.getLateFeeDailyRate()
                                    val displayStatus = if (isOverdue) "已逾期" else r.status
                                    Row(modifier = Modifier.fillMaxWidth().background(
                                        if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                    ).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(r.bookId, Modifier.weight(1.2f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(r.cardNo, Modifier.weight(0.7f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(r.borrowDate, Modifier.weight(0.7f), fontSize = 13.sp)
                                        Text(r.dueDate, Modifier.weight(0.7f), fontSize = 13.sp)
                                        Text(if (isOverdue) "${overdueDays}天" else displayStatus, Modifier.weight(0.5f), fontSize = 13.sp, color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                                        Text(if (isOverdue) "¥%.2f".format(fine) else "-", Modifier.weight(0.7f), fontSize = 13.sp, color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                                        Row(Modifier.weight(1.2f)) {
                                            if (r.status == "借阅中") {
                                                TextButton(onClick = {
                                                    BorrowService.returnBook(r.id, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                                    load()
                                                }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                                                    Text("归还", fontSize = 11.sp)
                                                }
                                                TextButton(onClick = { BorrowService.renew(r.id); load() }, contentPadding = PaddingValues(horizontal = 4.dp)) {
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
            }
        }
    }

    if (showDialog) {
        BorrowDialog(onDismiss = { showDialog = false }, onSaved = { showDialog = false; load() })
    }
}

@Composable
private fun BorrowDialog(onDismiss: () -> Unit, onSaved: () -> Unit) {
    // ─── 图书选择 ───────────────────────────────────────
    var bookSearch by remember { mutableStateOf("") }
    var bookResults by remember { mutableStateOf<List<Book>>(emptyList()) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var searchedBook by remember { mutableStateOf(false) }

    // ─── 会员选择 ───────────────────────────────────────
    var cardNo by remember { mutableStateOf("") }
    var memberInfo by remember { mutableStateOf<Member?>(null) }
    var activeCount by remember { mutableStateOf(0) }
    var memberSearched by remember { mutableStateOf(false) }

    // ─── 借阅配置 ───────────────────────────────────────
    var date by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var dueDate by remember { mutableStateOf(LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var message by remember { mutableStateOf<String?>(null) }

    fun searchBook() {
        searchedBook = true
        bookResults = if (bookSearch.isBlank()) BookService.listAll()
        else BookService.search(bookSearch)
    }

    fun searchMember() {
        memberSearched = true
        memberInfo = MemberService.getById(cardNo)
        if (memberInfo != null) {
            activeCount = BorrowService.getActiveBorrowCount(cardNo)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增借阅") },
        text = {
            Column(Modifier.widthIn(max = 520.dp)) {
                // ═══ 第一步：选择图书 ═══════════════════════
                Text("① 选择图书", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = if (selectedBook != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = bookSearch, onValueChange = { bookSearch = it; selectedBook = null },
                        placeholder = { Text("搜索图书编号/书名...") }, singleLine = true,
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Button(onClick = { searchBook() }, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("搜索") }
                }

                if (searchedBook && bookResults.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 140.dp)
                    ) {
                        LazyColumn(modifier = Modifier.padding(4.dp)) {
                            itemsIndexed(bookResults.take(20)) { _, book ->
                                val isSel = selectedBook?.bookId == book.bookId
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { selectedBook = book; message = null }
                                        .background(if (isSel) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(book.bookId, Modifier.weight(1f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(book.title, Modifier.weight(2f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("库存:${book.stock}", Modifier.weight(0.6f), fontSize = 11.sp, color = if (book.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
                if (searchedBook && bookResults.isEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("未找到匹配的图书", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                }

                // 选中图书摘要
                if (selectedBook != null) {
                    Spacer(Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(selectedBook!!.title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("编号: ${selectedBook!!.bookId}  |  作者: ${selectedBook!!.author ?: "-"}  |  库存: ${selectedBook!!.stock}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))

                // ═══ 第二步：选择会员 ═══════════════════════
                Text("② 选择会员", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = if (memberInfo != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = cardNo, onValueChange = { cardNo = it; memberInfo = null; memberSearched = false },
                        placeholder = { Text("输入会员卡号...") }, singleLine = true,
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Button(onClick = { searchMember() }, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("查询") }
                }

                if (memberInfo != null) {
                    Spacer(Modifier.height(6.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth()) {
                                Text(memberInfo!!.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("Lv.${memberInfo!!.level}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.weight(1f))
                                Text("已借 $activeCount / $MAX_BORROW", fontSize = 12.sp, color = if (activeCount >= MAX_BORROW) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                            }
                            Text("卡号: ${memberInfo!!.cardNo}  |  ${memberInfo!!.phone ?: "无电话"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else if (memberSearched) {
                    Spacer(Modifier.height(4.dp))
                    Text("未找到该会员", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))

                // ═══ 第三步：借阅配置 ═══════════════════════
                Text("③ 借阅配置", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    DateField(value = date, onValueChange = { date = it }, label = "借阅日期", modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    DateField(value = dueDate, onValueChange = { dueDate = it }, label = "到期日期", modifier = Modifier.weight(1f))
                }

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)) {
                        Text(message!!, modifier = Modifier.fillMaxWidth().padding(10.dp), fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedBook == null) { message = "请先选择图书"; return@Button }
                    if (memberInfo == null) { message = "请先查询并选择会员"; return@Button }
                    if (activeCount >= MAX_BORROW) { message = "该会员已借满 $MAX_BORROW 本，无法继续借阅"; return@Button }
                    val ok = BorrowService.borrow(selectedBook!!.bookId, memberInfo!!.cardNo, date, dueDate)
                    if (ok) onSaved() else message = "借阅失败（库存不足/信息有误）"
                },
                enabled = selectedBook != null && memberInfo != null
            ) { Text("确认借阅") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
