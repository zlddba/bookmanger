package org.zl.team.ui.stock

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
import org.zl.team.entity.Book
import org.zl.team.service.BookService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun StockStatScreen() {
    var books by remember { mutableStateOf(listOf<Book>()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            books = BookService.listAll()
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    val totalStock = books.sumOf { it.stock }
    val totalValue = books.sumOf { (it.price ?: 0.0) * it.stock }
    val outOfStock = books.count { it.stock <= 0 }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("库存统计", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> LoadingIndicator()
            loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
            books.isEmpty() -> EmptyHint("暂无数据")
            else -> {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard("图书种类", "${books.size}", MaterialTheme.colorScheme.primary)
                    StatCard("总库存", "$totalStock", MaterialTheme.colorScheme.tertiary)
                    StatCard("库存总价值", "¥%.2f".format(totalValue), MaterialTheme.colorScheme.secondary)
                    StatCard("缺货品种", "$outOfStock", if (outOfStock > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(16.dp))

                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column {
                        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("编号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("书名", Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("定价", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("库存", Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("库存价值", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        LazyColumn {
                            itemsIndexed(books) { index, b ->
                                Row(modifier = Modifier.fillMaxWidth().background(
                                    if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                ).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(b.bookId, Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(b.title, Modifier.weight(2f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(if (b.price != null) "¥%.2f".format(b.price) else "-", Modifier.weight(0.6f), fontSize = 13.sp)
                                    Text("${b.stock}", Modifier.weight(0.5f), fontSize = 13.sp, color = if (b.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    Text("¥%.2f".format((b.price ?: 0.0) * b.stock), Modifier.weight(0.8f), fontSize = 13.sp)
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
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
