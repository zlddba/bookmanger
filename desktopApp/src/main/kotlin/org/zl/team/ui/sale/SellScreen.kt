package org.zl.team.ui.sale

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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.Book
import org.zl.team.service.BookService
import org.zl.team.service.SaleService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.service.BookstoreInfoService
import org.zl.team.util.ReceiptPrinter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun SellScreen() {
    var keyword by remember { mutableStateOf("") }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }
    var quantity by remember { mutableStateOf("1") }
    var cardNo by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var lastPrintTitle by remember { mutableStateOf("") }
    var lastPrintQty by remember { mutableStateOf(0) }
    var lastPrintPrice by remember { mutableStateOf(0.0) }
    var lastPrintDate by remember { mutableStateOf("") }
    var showReceiptDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    var editableDate by remember { mutableStateOf(date) }

    fun search() {
        isLoading = true
        loadError = null
        try {
            books = if (keyword.isBlank()) BookService.listAll() else BookService.search(keyword)
        } catch (e: Exception) {
            loadError = "搜索失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { search() }

    Row(modifier = Modifier.fillMaxSize().padding(24.dp).onPreviewKeyEvent { event ->
        if (event.type == KeyEventType.KeyUp && event.key == Key.F8 && selectedBook != null && (quantity.toIntOrNull() ?: 0) > 0 && (quantity.toIntOrNull() ?: 0) <= (selectedBook?.stock ?: 0)) {
            val qty = quantity.toIntOrNull() ?: 0
            val book = selectedBook!!
            val ok = SaleService.sell(bookId = book.bookId, quantity = qty, cardNo = cardNo.ifBlank { null }, date = editableDate, remark = remark.ifBlank { null })
            if (ok) { statusMessage = "销售成功！"; isSuccess = true; lastPrintTitle = book.title; lastPrintQty = qty; lastPrintPrice = book.price ?: 0.0; lastPrintDate = editableDate; selectedBook = BookService.getById(book.bookId); search() }
            else { statusMessage = "销售失败，请检查数据"; isSuccess = false }
            true
        } else false
    }) {
        // ─── 左侧：图书选择 ─────────────────────────────
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text("图书销售", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                placeholder = { Text("搜索图书...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    TextButton(onClick = { search() }) { Text("搜索") }
                }
            )
            Spacer(Modifier.height(8.dp))

            // 书籍表格
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                when {
                    isLoading -> LoadingIndicator()
                    loadError != null -> ErrorBanner(loadError!!, onRetry = ::search)
                    books.isEmpty() -> EmptyHint("暂无图书数据")
                    else -> {
                        Column {
                            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Text("编号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("书名", Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("定价", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("库存", Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            LazyColumn {
                                itemsIndexed(books) { index, book ->
                                    val isSelected = selectedBook?.bookId == book.bookId
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { selectedBook = book }.background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                                index % 2 == 1 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        ).padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(book.bookId, Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(book.title, Modifier.weight(2f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("¥%.2f".format(book.price ?: 0.0), Modifier.weight(0.6f), fontSize = 13.sp)
                                        Text("${book.stock}", Modifier.weight(0.5f), fontSize = 13.sp, color = if (book.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.width(24.dp))

        // ─── 右侧：销售表单 ────────────────────────────
        Surface(
            modifier = Modifier.width(360.dp).fillMaxHeight(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Text("销售登记", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(20.dp))

                if (selectedBook != null) {
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        val book = selectedBook!!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(book.title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("编号: ${book.bookId}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("定价: ¥%.2f".format(book.price ?: 0.0), fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text("库存: ${book.stock}", fontSize = 12.sp, color = if (book.stock > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error)
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // 数量
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                        label = { Text("数量") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.height(12.dp))

                    // 会员卡号（可选）
                    OutlinedTextField(
                        value = cardNo,
                        onValueChange = { cardNo = it },
                        label = { Text("会员卡号（可选）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.height(12.dp))

                    // 日期
                    OutlinedTextField(
                        value = editableDate,
                        onValueChange = { editableDate = it },
                        label = { Text("日期") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.height(12.dp))

                    // 备注
                    OutlinedTextField(
                        value = remark,
                        onValueChange = { remark = it },
                        label = { Text("备注（可选）") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.height(20.dp))

                    // 金额预览
                    val qty = quantity.toIntOrNull() ?: 0
                    val unitPrice = book.price ?: 0.0
                    val subtotal = qty * unitPrice
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    ) {
                        Column(Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("单价", fontSize = 13.sp)
                                Text("¥%.2f".format(unitPrice), fontSize = 13.sp)
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("数量", fontSize = 13.sp)
                Text("${qty}", fontSize = 13.sp)
                            }
                            HorizontalDivider(Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("应收", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("¥%.2f".format(subtotal), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    // 操作按钮 + 反馈
                    Button(
                        onClick = {
                            if (qty <= 0) { statusMessage = "数量必须大于0"; isSuccess = false; return@Button }
                            if (qty > book.stock) { statusMessage = "库存不足（剩余 ${book.stock}）"; isSuccess = false; return@Button }
                            val ok = SaleService.sell(
                                bookId = book.bookId,
                                quantity = qty,
                                cardNo = cardNo.ifBlank { null },
                                date = editableDate,
                                remark = remark.ifBlank { null }
                            )
                            if (ok) {
                                statusMessage = "销售成功！"
                                isSuccess = true
                                lastPrintTitle = book.title
                                lastPrintQty = qty
                                lastPrintPrice = unitPrice
                                lastPrintDate = editableDate
                                // 刷新库存
                                selectedBook = BookService.getById(book.bookId)
                                search()
                            } else {
                                statusMessage = "销售失败，请检查数据"
                                isSuccess = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = selectedBook != null && qty > 0 && qty <= (selectedBook?.stock ?: 0)
                    ) {
                        Text("确认销售", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    if (statusMessage != null) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = (if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error).copy(alpha = 0.15f)
                        ) {
                            Text(
                                statusMessage!!,
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                fontSize = 13.sp,
                                color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                        if (isSuccess && lastPrintQty > 0) {
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { showReceiptDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("打印小票", fontSize = 13.sp) }
                        }
                    }
                    }
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "请在左侧选择图书",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }

    if (showReceiptDialog) {
        val storeInfo = remember { BookstoreInfoService.getInfo() }
        val total = lastPrintQty * lastPrintPrice

        AlertDialog(
            onDismissRequest = { showReceiptDialog = false },
            title = null,
            text = {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .widthIn(min = 340.dp, max = 420.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // ─── 店名 ─────────────────────────
                        Text(
                            storeInfo?.name ?: "图书管理系统",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (storeInfo?.phone != null || storeInfo?.address != null) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                listOfNotNull(storeInfo.phone, storeInfo.address).joinToString(" | "),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(12.dp))

                        // ─── 分隔线 ───────────────────────
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(Modifier.height(8.dp))

                        // ─── 表头：单号 + 日期 ─────────────
                        Text("销售小票", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text("日期: $lastPrintDate", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(12.dp))

                        // ─── 表格 ─────────────────────────
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text("品名", Modifier.weight(2.2f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("数量", Modifier.weight(0.6f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("单价", Modifier.weight(0.8f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("金额", Modifier.weight(1f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))

                        // 商品行
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(lastPrintTitle, Modifier.weight(2.2f), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text("$lastPrintQty", Modifier.weight(0.6f), fontSize = 12.sp)
                            Text("¥%.2f".format(lastPrintPrice), Modifier.weight(0.8f), fontSize = 12.sp)
                            Text("¥%.2f".format(total), Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Spacer(Modifier.height(8.dp))

                        // ─── 合计 ─────────────────────────
                        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                            Spacer(Modifier.weight(1f))
                            Text("合计:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(24.dp))
                            Text("¥%.2f".format(total), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(Modifier.height(20.dp))
                        Text("─".repeat(36), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                        Spacer(Modifier.height(8.dp))
                        Text("感谢您的光临！", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(20.dp))

                        // ─── 系统打印按钮 ──────────────────
                        OutlinedButton(
                            onClick = { ReceiptPrinter.print(lastPrintTitle, lastPrintQty, lastPrintPrice, total, lastPrintDate) },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("系统打印", fontSize = 14.sp) }

                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = { showReceiptDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("关闭") }
                    }
                }
            },
            confirmButton = {},
            shape = RoundedCornerShape(12.dp)
        )
    }
}
