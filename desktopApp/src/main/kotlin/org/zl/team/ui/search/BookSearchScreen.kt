package org.zl.team.ui.search

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
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.HighlightedText
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.PaginationBar
import org.zl.team.ui.components.page
import org.zl.team.util.CsvExporter

@Composable
fun BookSearchScreen() {
    var keyword by remember { mutableStateOf("") }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var hasSearched by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }
    // 分页
    var currentPage by remember { mutableStateOf(0) }
    val pageSize = 20
    val totalPages = if (books.isEmpty()) 1 else (books.size + pageSize - 1) / pageSize
    val pagedBooks = books.page(currentPage, pageSize)

    fun doSearch() {
        currentPage = 0
        isLoading = true
        loadError = null
        hasSearched = true
        try {
            books = if (keyword.isBlank()) {
                BookService.listAll()
            } else {
                BookService.search(keyword)
            }
        } catch (e: Exception) {
            loadError = "搜索失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { doSearch() }

    Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text("图书检索", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                placeholder = { Text("搜索书名、作者、出版社、ISBN...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { TextButton(onClick = { doSearch() }) { Text("搜索") } }
            )
            Spacer(Modifier.height(16.dp))

            when {
                isLoading -> LoadingIndicator()
                loadError != null -> ErrorBanner(loadError!!, onRetry = ::doSearch)
                books.isEmpty() && hasSearched -> EmptyHint("未找到匹配的图书")
                else -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("共 ${books.size} 条结果", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.weight(1f))
                        OutlinedButton(
                            onClick = {
                                CsvExporter.export(
                                    listOf("编号", "书名", "丛书", "作者", "出版社", "版次", "ISBN", "分类号", "定价", "库存", "出版日期", "关键词"),
                                    books.map { b -> listOf(b.bookId, b.title, b.series, b.author, b.publisher, b.edition, b.isbn, b.categoryId, b.price?.let { "¥%.2f".format(it) }, "${b.stock}", b.publishDate, b.keywords) },
                                    "BookSearch_${java.time.LocalDate.now()}.csv"
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) { Text("导出 CSV", fontSize = 12.sp) }
                    }
                    Spacer(Modifier.height(8.dp))
                    BookTable(books = pagedBooks, keyword = keyword, selectedId = selectedBook?.bookId, onSelect = { selectedBook = it })
                    PaginationBar(currentPage, totalPages, { currentPage = it })
                }
            }
        }

        if (selectedBook != null) {
            Spacer(Modifier.width(24.dp))
            BookDetailPanel(book = selectedBook!!)
        }
    }
}

@Composable
private fun BookTable(
    books: List<Book>,
    keyword: String,
    selectedId: String?,
    onSelect: (Book) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(Modifier.fillMaxWidth()) {
            // 表头
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("编号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("书名", Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("作者", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("出版社", Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("定价", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("库存", Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            LazyColumn {
                itemsIndexed(books) { index, book ->
                    val isSelected = book.bookId == selectedId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(book) }
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                    index % 2 == 1 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HighlightedText(book.bookId, keyword, Modifier.weight(0.8f))
                        HighlightedText(book.title, keyword, Modifier.weight(2f))
                        HighlightedText(book.author ?: "-", keyword, Modifier.weight(1.2f))
                        HighlightedText(book.publisher ?: "-", keyword, Modifier.weight(1.5f))
                        Text(book.price?.let { "¥%.2f".format(it) } ?: "-", Modifier.weight(0.6f), fontSize = 13.sp)
                        Text("${book.stock}", Modifier.weight(0.5f), fontSize = 13.sp, color = if (book.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BookDetailPanel(book: Book) {
    Surface(
        modifier = Modifier.width(320.dp).fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
        ) {
            Text("图书详情", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(20.dp))

            DetailRow("编号", book.bookId)
            DetailRow("书名", book.title)
            DetailRow("丛书", book.series)
            DetailRow("作者", book.author)
            DetailRow("出版社", book.publisher)
            DetailRow("版次", book.edition)
            DetailRow("ISBN", book.isbn)
            DetailRow("分类号", book.categoryId)
            DetailRow("定价", book.price?.let { "¥%.2f".format(it) })
            DetailRow("库存", "${book.stock}")
            DetailRow("出版日期", book.publishDate)
            DetailRow("入库时间", book.createdAt)

            if (!book.keywords.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text("关键词", fontWeight = FontWeight.Medium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(book.keywords!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            }

            if (!book.description.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text("内容简介", fontWeight = FontWeight.Medium, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(book.description!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    if (value != null) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(72.dp))
            Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        }
    }
}
