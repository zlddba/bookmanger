package org.zl.team.ui.purchase

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.Book
import org.zl.team.entity.PurchaseRecord
import org.zl.team.entity.Supplier
import org.zl.team.service.BookService
import org.zl.team.service.PurchaseService
import org.zl.team.service.SupplierService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.DateField
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PurchaseRegisterScreen() {
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var suppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var supplierId by remember { mutableStateOf("") }
    var bookKeyword by remember { mutableStateOf("") }
    var selectedBookId by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unitPrice by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("1.0") }
    var date by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var remark by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var recentRecords by remember { mutableStateOf<List<PurchaseRecord>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoading = true
        loadError = null
        try {
            books = BookService.listAll()
            suppliers = SupplierService.listAll()
            recentRecords = PurchaseService.listAll().takeLast(10).reversed()
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val filteredBooks = remember(bookKeyword, books) {
        if (bookKeyword.isBlank()) books else books.filter {
            it.bookId.contains(bookKeyword, ignoreCase = true) || it.title.contains(bookKeyword, ignoreCase = true)
        }
    }

    Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Text("新书入库", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(Modifier.padding(24.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("选择图书", fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        Spacer(Modifier.width(12.dp))
                        OutlinedTextField(value = bookKeyword, onValueChange = { bookKeyword = it }, placeholder = { Text("搜索图书...") }, singleLine = true, modifier = Modifier.width(200.dp), shape = RoundedCornerShape(10.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth().weight(1f)) {
                        when {
                            isLoading -> LoadingIndicator()
                            loadError != null -> ErrorBanner(loadError!!, onRetry = {
                                isLoading = true
                                loadError = null
                                try {
                                    books = BookService.listAll()
                                    suppliers = SupplierService.listAll()
                                    recentRecords = PurchaseService.listAll().takeLast(10).reversed()
                                } catch (e: Exception) {
                                    loadError = "加载失败: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            })
                            filteredBooks.isEmpty() -> EmptyHint("暂无图书数据")
                            else -> LazyColumn {
                                itemsIndexed(filteredBooks) { _, book ->
                                    val isSel = selectedBookId == book.bookId
                                    Surface(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSel) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(book.bookId, Modifier.weight(0.8f), fontSize = 12.sp)
                                            Text(book.title, Modifier.weight(2f), fontSize = 12.sp, maxLines = 1)
                                            Text("库存: ${book.stock}", Modifier.weight(0.5f), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("¥%.2f".format(book.price ?: 0.0), Modifier.weight(0.5f), fontSize = 12.sp)
                                            Button(onClick = { selectedBookId = book.bookId }, shape = RoundedCornerShape(6.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                                                Text("选择", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.width(24.dp))
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.width(360.dp).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().padding(24.dp)) {
                Text("入库登记", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(20.dp))

                Text("供应商", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                var supplierExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = suppliers.find { it.supplierId == supplierId }?.name ?: supplierId.ifBlank { "请选择" },
                        onValueChange = {}, readOnly = true, singleLine = true,
                        modifier = Modifier.fillMaxWidth().clickable { supplierExpanded = true },
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = { Text(if (supplierExpanded) "▾" else "▸", fontSize = 10.sp) }
                    )
                    DropdownMenu(expanded = supplierExpanded, onDismissRequest = { supplierExpanded = false }, modifier = Modifier.widthIn(max = 340.dp)) {
                        suppliers.forEach { s ->
                            DropdownMenuItem(
                                text = { Text("${s.name}(${s.supplierId})", fontSize = 13.sp) },
                                onClick = { supplierId = s.supplierId; supplierExpanded = false }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))

                if (selectedBookId.isNotBlank()) {
                    val book = books.find { it.bookId == selectedBookId }
                    if (book != null) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)), shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(book.title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text("编号: ${book.bookId} | 定价: ¥%.2f".format(book.price ?: 0.0), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it.filter { c -> c.isDigit() } }, label = { Text("数量") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = unitPrice, onValueChange = { unitPrice = it }, label = { Text("单价(进价)") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = discount, onValueChange = { discount = it }, label = { Text("折扣") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    DateField(value = date, onValueChange = { date = it }, label = "日期", modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(value = remark, onValueChange = { remark = it }, label = { Text("备注（可选）") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(20.dp))

                val qty = quantity.toIntOrNull() ?: 0
                val price = unitPrice.toDoubleOrNull() ?: 0.0
                val disc = discount.toDoubleOrNull() ?: 1.0
                val subtotal = qty * price * disc
                Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("数量×单价×折扣", fontSize = 13.sp)
                            Text("$qty × ¥$price × $disc", fontSize = 13.sp)
                        }
                        HorizontalDivider(Modifier.padding(vertical = 6.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("金额", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("¥%.2f".format(subtotal), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (supplierId.isBlank() || selectedBookId.isBlank() || qty <= 0 || price <= 0.0) {
                            statusMessage = "请完整填写供应商、图书、数量和单价"; isSuccess = false
                            return@Button
                        }
                        val ok = PurchaseService.register(supplierId, selectedBookId, qty, price, disc, date, remark.ifBlank { null })
                        if (ok) {
                            statusMessage = "入库成功！"; isSuccess = true
                            recentRecords = PurchaseService.listAll().takeLast(10).reversed()
                            quantity = "1"; unitPrice = ""; discount = "1.0"; remark = ""
                        } else {
                            statusMessage = "入库失败，请检查数据"; isSuccess = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp),
                    enabled = supplierId.isNotBlank() && selectedBookId.isNotBlank() && qty > 0 && price > 0.0
                ) { Text("确认入库", fontSize = 16.sp, fontWeight = FontWeight.Medium) }

                if (statusMessage != null) {
                    Spacer(Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = (if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error).copy(alpha = 0.15f)) {
                        Text(statusMessage!!, modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 13.sp, color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(Modifier.weight(1f))
                Text("最近入库记录", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                recentRecords.forEach { r ->
                    Text(
                        "${r.date} ${r.bookId} x${r.quantity} ¥%.2f".format(r.amount ?: 0.0),
                        fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
