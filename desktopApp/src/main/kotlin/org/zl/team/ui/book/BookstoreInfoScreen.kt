package org.zl.team.ui.book

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.BookstoreInfo
import org.zl.team.service.BookstoreInfoService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun BookstoreInfoScreen() {
    var info by remember { mutableStateOf<BookstoreInfo?>(null) }
    var editing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            info = BookstoreInfoService.getInfo()
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    fun startEdit() {
        val i = info ?: return
        name = i.name; address = i.address ?: ""; website = i.website ?: ""
        contact = i.contact ?: ""; phone = i.phone ?: ""; mobile = i.mobile ?: ""
        email = i.email ?: ""; description = i.description ?: ""; remark = i.remark ?: ""
        editing = true
    }

    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        when {
            isLoading -> LoadingIndicator()
            loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
            info == null -> EmptyHint("暂无书店信息")
            editing -> {
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.widthIn(max = 500.dp).align(Alignment.TopCenter)) {
                    Column(Modifier.padding(32.dp)) {
                        Text("编辑书店信息", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(20.dp))
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("书店名称") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth()) {
                            OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("联系人") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("电话") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth()) {
                            OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("手机") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("邮箱") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("地址") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = website, onValueChange = { website = it }, label = { Text("网址") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("简介") }, modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = remark, onValueChange = { remark = it }, label = { Text("备注") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = {
                            if (name.isBlank()) return@Button
                            BookstoreInfoService.update(BookstoreInfo(name, address.ifBlank { null }, website.ifBlank { null }, contact.ifBlank { null }, phone.ifBlank { null }, mobile.ifBlank { null }, email.ifBlank { null }, description.ifBlank { null }, remark.ifBlank { null }))
                            info = BookstoreInfoService.getInfo()
                            editing = false
                        }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) { Text("保存") }
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = { editing = false }, modifier = Modifier.fillMaxWidth()) { Text("取消") }
                    }
                }
            }
            else -> {
                val i = info!!
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.widthIn(max = 500.dp).align(Alignment.TopCenter)) {
                    Column(Modifier.padding(32.dp)) {
                        Text(i.name, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.height(8.dp))
                        val desc = i.description
                        if (!desc.isNullOrBlank()) {
                            Text(desc, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(16.dp))
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Spacer(Modifier.height(16.dp))
                        InfoRow("地址", i.address)
                        InfoRow("网址", i.website)
                        InfoRow("联系人", i.contact)
                        InfoRow("电话", i.phone)
                        InfoRow("手机", i.mobile)
                        InfoRow("邮箱", i.email)
                        InfoRow("备注", i.remark)
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = ::startEdit, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) { Text("编辑信息") }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("$label:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(64.dp))
            Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        }
    }
}
