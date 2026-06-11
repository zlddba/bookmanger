package org.zl.team.ui.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.BookReservation
import org.zl.team.entity.Book
import org.zl.team.entity.Member
import org.zl.team.service.BookService
import org.zl.team.service.MemberService
import org.zl.team.service.ReservationService
import org.zl.team.util.SessionManager
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun ReservationScreen() {
    var reservations by remember { mutableStateOf<List<BookReservation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var statusFilter by remember { mutableStateOf("") }

    fun load() {
        isLoading = true
        loadError = null
        try {
            reservations = if (statusFilter.isBlank()) ReservationService.listAll() else ReservationService.listByStatus(statusFilter)
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
                Text("图书预订", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.weight(1f))
                var filterExpanded by remember { mutableStateOf(false) }
                Box {
                    FilterChip(
                        selected = statusFilter.isNotBlank(),
                        onClick = { filterExpanded = true },
                        label = { Text(if (statusFilter.isNotBlank()) "状态: $statusFilter" else "全部", fontSize = 13.sp) }
                    )
                    DropdownMenu(expanded = filterExpanded, onDismissRequest = { filterExpanded = false }) {
                        DropdownMenuItem(text = { Text("全部") }, onClick = { statusFilter = ""; filterExpanded = false; load() })
                        DropdownMenuItem(text = { Text("待处理") }, onClick = { statusFilter = "待处理"; filterExpanded = false; load() })
                        DropdownMenuItem(text = { Text("已到货") }, onClick = { statusFilter = "已到货"; filterExpanded = false; load() })
                        DropdownMenuItem(text = { Text("已取消") }, onClick = { statusFilter = "已取消"; filterExpanded = false; load() })
                    }
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { showDialog = true }, shape = RoundedCornerShape(10.dp)) { Text("新建预订") }
            }
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column {
                    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text("客户", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("书名", Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("作者", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("电话", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("状态", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("操作", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    when {
                        isLoading -> LoadingIndicator()
                        loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
                        reservations.isEmpty() -> EmptyHint("暂无数据")
                        else -> {
                            LazyColumn {
                                itemsIndexed(reservations) { index, r ->
                                    Row(modifier = Modifier.fillMaxWidth().background(
                                        if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                    ).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(r.customerName, Modifier.weight(1f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(r.bookTitle, Modifier.weight(2f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(r.author ?: "-", Modifier.weight(1f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(r.customerPhone ?: "-", Modifier.weight(1f), fontSize = 13.sp)
                                        Text(r.status, Modifier.weight(0.8f), fontSize = 13.sp, color = when (r.status) {
                                            "待处理" -> MaterialTheme.colorScheme.error
                                            "已到货" -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        })
                                        Row(Modifier.weight(1f)) {
                                            if (r.status == "待处理") {
                                                TextButton(onClick = { ReservationService.markCompleted(r.id); load() }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                                                    Text("到货", fontSize = 11.sp)
                                                }
                                                TextButton(onClick = { ReservationService.cancel(r.id); load() }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                                                    Text("取消", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
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
        ReservationDialog(
            onDismiss = { showDialog = false },
            onSaved = { showDialog = false; load() }
        )
    }
}

@Composable
private fun ReservationDialog(onDismiss: () -> Unit, onSaved: () -> Unit) {
    // ─── 图书信息 ───────────────────────────────────────
    var bookSearch by remember { mutableStateOf("") }
    var bookResults by remember { mutableStateOf<List<Book>>(emptyList()) }
    var searched by remember { mutableStateOf(false) }
    var bookTitle by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var publisher by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }

    // ─── 客户信息 ───────────────────────────────────────
    var cardNo by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf(
        if (SessionManager.currentUserRole == "会员") SessionManager.currentUserName else ""
    ) }
    var customerPhone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    fun searchBook() {
        searched = true
        bookResults = if (bookSearch.isBlank()) BookService.listAll() else BookService.search(bookSearch)
    }

    fun lookupMember() {
        val member = MemberService.getById(cardNo)
        if (member != null) {
            customerName = member.name
            customerPhone = member.phone ?: ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建预订") },
        text = {
            Column(Modifier.widthIn(max = 520.dp)) {
                // ═══ 快速选择图书 ═══════════════════════════
                Text("快速选择图书", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = bookSearch, onValueChange = { bookSearch = it },
                        placeholder = { Text("搜索图书编号/书名...") }, singleLine = true,
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Button(onClick = { searchBook() }, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("搜索") }
                }
                if (searched && bookResults.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth().heightIn(max = 120.dp)) {
                        LazyColumn(modifier = Modifier.padding(4.dp)) {
                            itemsIndexed(bookResults.take(20)) { _, b ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).clickable {
                                        bookTitle = b.title; author = b.author ?: ""; publisher = b.publisher ?: ""; isbn = b.isbn ?: ""
                                    }.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(b.bookId, Modifier.weight(0.6f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(b.title, Modifier.weight(1f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(b.author ?: "-", Modifier.weight(0.6f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))

                // ═══ 快速查询会员 ═══════════════════════════
                Text("快速查询会员", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = cardNo, onValueChange = { cardNo = it },
                        placeholder = { Text("输入会员卡号自动填入...") }, singleLine = true,
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Button(onClick = { lookupMember() }, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 12.dp)) { Text("查询") }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))

                // ═══ 预订信息表单 ═══════════════════════════
                Text("预订信息", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = bookTitle, onValueChange = { bookTitle = it }, label = { Text("书名*") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("作者") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = publisher, onValueChange = { publisher = it }, label = { Text("出版社") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("客户名*") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = customerPhone, onValueChange = { customerPhone = it }, label = { Text("电话") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = isbn, onValueChange = { isbn = it }, label = { Text("ISBN") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("备注") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (bookTitle.isBlank() || customerName.isBlank()) return@Button
                val ok = ReservationService.create(BookReservation(0, bookTitle, author.ifBlank { null }, publisher.ifBlank { null }, isbn.ifBlank { null }, customerName, customerPhone.ifBlank { null }, "待处理", note.ifBlank { null }))
                if (ok) onSaved()
            }) { Text("创建") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
