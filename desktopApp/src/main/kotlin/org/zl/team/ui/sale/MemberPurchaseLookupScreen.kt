package org.zl.team.ui.sale

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
import org.zl.team.entity.SaleRecord
import org.zl.team.service.BookService
import org.zl.team.service.SaleService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun MemberPurchaseLookupScreen() {
    var cardNo by remember { mutableStateOf("") }
    var records by remember { mutableStateOf<List<SaleRecord>>(emptyList()) }
    var bookTitles by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var hasSearched by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun doSearch() {
        if (cardNo.isBlank()) return
        isLoading = true
        loadError = null
        hasSearched = true
        try {
            val list = SaleService.listByMember(cardNo)
            val titles = list.mapNotNull { it.bookId }.distinct().associateWith { bid ->
                BookService.getById(bid)?.title ?: bid
            }
            records = list
            bookTitles = titles
        } catch (e: Exception) {
            loadError = "查询失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("会员消费查询", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = cardNo,
                onValueChange = { cardNo = it },
                label = { Text("会员卡号") },
                singleLine = true,
                modifier = Modifier.width(280.dp),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.width(12.dp))
            Button(onClick = { doSearch() }, shape = RoundedCornerShape(10.dp), enabled = cardNo.isNotBlank()) {
                Text("查询")
            }
        }
        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> LoadingIndicator()
            loadError != null -> ErrorBanner(loadError!!, onRetry = ::doSearch)
            !hasSearched -> EmptyHint("请输入会员卡号查询消费记录")
            records.isEmpty() -> EmptyHint("该会员暂无消费记录")
            else -> {
                var total = 0.0
                records.forEach { total += it.amount ?: 0.0 }
                Text("共 ${records.size} 笔, 合计 ¥${"%.2f".format(total)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))

                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
                    Column(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("日期", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("书名", Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("数量", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("折扣", Modifier.weight(0.6f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("金额", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("备注", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        records.forEachIndexed { index, r ->
                            Row(Modifier.fillMaxWidth().background(
                                if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(r.date ?: "-", Modifier.weight(1.2f), fontSize = 13.sp)
                                Text(r.bookId?.let { bookTitles[it] ?: it } ?: "-", Modifier.weight(2f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${r.quantity ?: 0}", Modifier.weight(0.6f), fontSize = 13.sp)
                                Text("${(r.discount * 100).toInt()}%", Modifier.weight(0.6f), fontSize = 13.sp)
                                Text("¥${"%.2f".format(r.amount ?: 0.0)}", Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                                Text(r.remark ?: "-", Modifier.weight(1f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                        }
                    }
                }
            }
        }
    }
}
