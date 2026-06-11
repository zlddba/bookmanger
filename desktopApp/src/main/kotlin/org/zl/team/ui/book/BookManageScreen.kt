package org.zl.team.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.Book
import org.zl.team.service.BookService
import org.zl.team.service.CategoryService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.DateField
import org.zl.team.ui.components.PaginationBar
import org.zl.team.ui.components.page
import org.zl.team.util.CsvImporter

@Composable
fun BookManageScreen() {
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var selected by remember { mutableStateOf<Book?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Book?>(null) }
    var keyword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var importResult by remember { mutableStateOf<CsvImporter.ImportResult?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    // 分页
    var currentPage by remember { mutableStateOf(0) }
    val pageSize = 20
    val totalPages = if (books.isEmpty()) 1 else (books.size + pageSize - 1) / pageSize
    val pagedBooks = books.page(currentPage, pageSize)

    fun load() {
        currentPage = 0
        isLoading = true
        loadError = null
        try {
            books = if (keyword.isBlank()) BookService.listAll() else BookService.search(keyword)
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
                Text("图书资料", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.width(16.dp))
                OutlinedTextField(
                    value = keyword, onValueChange = { keyword = it },
                    placeholder = { Text("搜索图书...") }, singleLine = true,
                    modifier = Modifier.width(240.dp), shape = RoundedCornerShape(10.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { load() }, shape = RoundedCornerShape(10.dp)) { Text("搜索") }
                Spacer(Modifier.weight(1f))
                Button(onClick = { editing = null; showDialog = true }, shape = RoundedCornerShape(10.dp)) {
                    Text("新增图书")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = {
                    val result = CsvImporter.importAndSave { BookService.create(it) }
                    importResult = result
                    load()
                }, shape = RoundedCornerShape(10.dp)) { Text("导入 CSV") }
            }
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    isLoading -> LoadingIndicator()
                    loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
                    books.isEmpty() -> EmptyHint(if (keyword.isBlank()) "暂无图书数据" else "未找到匹配的图书")
                    else -> {
                        Column {
                            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Text("编号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("书名", Modifier.weight(1.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("作者", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("出版社", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("定价", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("库存", Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            LazyColumn {
                                itemsIndexed(pagedBooks) { index, b ->
                                    val isSel = selected?.bookId == b.bookId
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { selected = b }.background(
                                            when { isSel -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f); index % 2 == 1 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f); else -> MaterialTheme.colorScheme.surface }
                                        ).padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(b.bookId, Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(b.title, Modifier.weight(1.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(b.author ?: "-", Modifier.weight(1f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(b.publisher ?: "-", Modifier.weight(1.2f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(if (b.price != null) "¥%.2f".format(b.price) else "-", Modifier.weight(0.6f), fontSize = 13.sp)
                                        Text("${b.stock}", Modifier.weight(0.5f), fontSize = 13.sp, color = if (b.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                                }
                            }
                            PaginationBar(currentPage, totalPages, { currentPage = it })
                        }
                    }
                }
            }
        }

        if (selected != null) {
            Spacer(Modifier.width(24.dp))
            BookDetailPanel(
                book = selected!!,
                onEdit = { editing = selected; showDialog = true },
                onDelete = { showDeleteConfirm = true }
            )
        }
    }

    if (showDialog) {
        BookDialog(
            book = editing,
            onDismiss = { showDialog = false },
            onSaved = { showDialog = false; load() }
        )
    }

    importResult?.let { r ->
        AlertDialog(
            onDismissRequest = { importResult = null },
            title = { Text("导入结果") },
            text = {
                Column {
                    Text("成功: ${r.successCount} 条")
                    Text("失败: ${r.failCount} 条")
                    if (r.errors.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("错误详情:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f)) {
                            Column(Modifier.heightIn(max = 200.dp).verticalScroll(rememberScrollState()).padding(8.dp)) {
                                r.errors.take(20).forEach { Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.error) }
                                if (r.errors.size > 20) Text("...还有 ${r.errors.size - 20} 条", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { importResult = null }) { Text("确定") } }
        )
    }

    if (showDeleteConfirm && selected != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除图书「${selected!!.title}」吗？此操作不可撤销。") },
            confirmButton = {
                Button(onClick = {
                    BookService.delete(selected!!.bookId)
                    showDeleteConfirm = false; selected = null; load()
                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("删除")
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("取消") } }
        )
    }
}

@Composable
private fun BookDetailPanel(book: Book, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(modifier = Modifier.width(300.dp).fillMaxHeight(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text("图书详情", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            DetailItem("编号", book.bookId)
            DetailItem("书名", book.title)
            DetailItem("分类", book.categoryId)
            DetailItem("丛书", book.series)
            DetailItem("作者", book.author)
            DetailItem("出版社", book.publisher)
            DetailItem("版次", book.edition)
            DetailItem("ISBN", book.isbn)
            DetailItem("定价", if (book.price != null) "¥%.2f".format(book.price) else null)
            DetailItem("库存", book.stock.toString())
            DetailItem("关键词", book.keywords)
            DetailItem("出版日期", book.publishDate)
            DetailItem("入库时间", book.createdAt)
            Spacer(Modifier.weight(1f))
            Button(onClick = onEdit, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) { Text("编辑") }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                Text("删除", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun BookDialog(book: Book?, onDismiss: () -> Unit, onSaved: () -> Unit) {
    val isNew = book == null
    var bid by remember { mutableStateOf(book?.bookId ?: "") }
    var title by remember { mutableStateOf(book?.title ?: "") }
    var categoryId by remember { mutableStateOf(book?.categoryId ?: "") }
    var series by remember { mutableStateOf(book?.series ?: "") }
    var author by remember { mutableStateOf(book?.author ?: "") }
    var publisher by remember { mutableStateOf(book?.publisher ?: "") }
    var edition by remember { mutableStateOf(book?.edition ?: "") }
    var isbn by remember { mutableStateOf(book?.isbn ?: "") }
    var price by remember { mutableStateOf(book?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(book?.stock?.toString() ?: "0") }
    var keywords by remember { mutableStateOf(book?.keywords ?: "") }
    var publishDate by remember { mutableStateOf(book?.publishDate ?: "") }
    var description by remember { mutableStateOf(book?.description ?: "") }

    val categories = remember { CategoryService.listAll() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "新增图书" else "编辑图书") },
        text = {
            Column(Modifier.widthIn(max = 480.dp)) {
                if (isNew) {
                    OutlinedTextField(value = bid, onValueChange = { bid = it }, label = { Text("编号") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("书名") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("作者") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = publisher, onValueChange = { publisher = it }, label = { Text("出版社") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = categoryId, onValueChange = { categoryId = it }, label = { Text("分类号") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = edition, onValueChange = { edition = it }, label = { Text("版次") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = series, onValueChange = { series = it }, label = { Text("丛书") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = isbn, onValueChange = { isbn = it }, label = { Text("ISBN") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("定价") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = stock, onValueChange = { stock = it.filter { c -> c.isDigit() } }, label = { Text("库存") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    DateField(value = publishDate, onValueChange = { publishDate = it }, label = "出版日期", modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = keywords, onValueChange = { keywords = it }, label = { Text("关键词") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("内容简介") }, modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(10.dp))

                if (categories.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("可选分类", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        categories.take(8).forEach { cat ->
                            FilterChip(
                                selected = categoryId == cat.categoryId,
                                onClick = { categoryId = cat.categoryId },
                                label = { Text("${cat.name}(${cat.categoryId})", fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (bid.isBlank() || title.isBlank()) return@Button
                val priceVal = price.toDoubleOrNull()
                val stockVal = stock.toIntOrNull() ?: 0
                val ok = if (isNew) BookService.create(Book(bid, categoryId.ifBlank { null }, title, series.ifBlank { null }, author.ifBlank { null }, publisher.ifBlank { null }, edition.ifBlank { null }, isbn.ifBlank { null }, priceVal, stockVal, description.ifBlank { null }, keywords.ifBlank { null }, publishDate.ifBlank { null }, null))
                else BookService.update(book.copy(title = title, categoryId = categoryId.ifBlank { null }, series = series.ifBlank { null }, author = author.ifBlank { null }, publisher = publisher.ifBlank { null }, edition = edition.ifBlank { null }, isbn = isbn.ifBlank { null }, price = priceVal, stock = stockVal, description = description.ifBlank { null }, keywords = keywords.ifBlank { null }, publishDate = publishDate.ifBlank { null }))
                if (ok) onSaved()
            }) { Text(if (isNew) "创建" else "保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun DetailItem(label: String, value: String?) {
    if (value != null && value.isNotBlank()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
            Text("$label:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(56.dp))
            Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        }
    }
}
