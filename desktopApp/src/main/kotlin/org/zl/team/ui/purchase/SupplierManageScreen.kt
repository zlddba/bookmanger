package org.zl.team.ui.purchase

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.Supplier
import org.zl.team.service.SupplierService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun SupplierManageScreen() {
    var suppliers by remember { mutableStateOf<List<Supplier>>(emptyList()) }
    var selected by remember { mutableStateOf<Supplier?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Supplier?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            suppliers = SupplierService.listAll()
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    when {
        isLoading -> LoadingIndicator()
        loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
        suppliers.isEmpty() -> EmptyHint("暂无供应商数据")
        else -> {
            Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("供应商管理", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(Modifier.weight(1f))
                        Button(onClick = { editing = null; showDialog = true }, shape = RoundedCornerShape(10.dp)) {
                            Text("新增供应商")
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Column {
                            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Text("编号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("名称", Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("联系人", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("电话", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            LazyColumn {
                                itemsIndexed(suppliers) { index, s ->
                                    val isSel = selected?.supplierId == s.supplierId
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { selected = s }.background(
                                            when { isSel -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f); index % 2 == 1 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f); else -> MaterialTheme.colorScheme.surface }
                                        ).padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(s.supplierId, Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(s.name, Modifier.weight(1.5f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(s.contact ?: "-", Modifier.weight(0.8f), fontSize = 13.sp)
                                        Text(s.phone ?: "-", Modifier.weight(1f), fontSize = 13.sp)
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                                }
                            }
                        }
                    }
                }

                if (selected != null) {
                    Spacer(Modifier.width(24.dp))
                    SupplierDetailPanel(
                        supplier = selected!!,
                        onEdit = { editing = selected; showDialog = true },
                        onDelete = {
                            SupplierService.delete(selected!!.supplierId)
                            selected = null; load()
                        }
                    )
                }
            }

            if (showDialog) {
                SupplierDialog(
                    supplier = editing,
                    onDismiss = { showDialog = false },
                    onSaved = { showDialog = false; load() }
                )
            }
        }
    }
}

@Composable
private fun SupplierDetailPanel(supplier: Supplier, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(modifier = Modifier.width(300.dp).fillMaxHeight(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text("供应商详情", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            DetailRow("编号", supplier.supplierId)
            DetailRow("名称", supplier.name)
            DetailRow("联系人", supplier.contact)
            DetailRow("电话", supplier.phone)
            DetailRow("传真", supplier.fax)
            DetailRow("邮箱", supplier.email)
            DetailRow("地址", supplier.address)
            DetailRow("网址", supplier.website)
            DetailRow("简介", supplier.description)
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
private fun SupplierDialog(supplier: Supplier?, onDismiss: () -> Unit, onSaved: () -> Unit) {
    val isNew = supplier == null
    var sid by remember { mutableStateOf(supplier?.supplierId ?: "") }
    var name by remember { mutableStateOf(supplier?.name ?: "") }
    var contact by remember { mutableStateOf(supplier?.contact ?: "") }
    var phone by remember { mutableStateOf(supplier?.phone ?: "") }
    var fax by remember { mutableStateOf(supplier?.fax ?: "") }
    var email by remember { mutableStateOf(supplier?.email ?: "") }
    var address by remember { mutableStateOf(supplier?.address ?: "") }
    var website by remember { mutableStateOf(supplier?.website ?: "") }
    var desc by remember { mutableStateOf(supplier?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "新增供应商" else "编辑供应商") },
        text = {
            Column(Modifier.widthIn(max = 400.dp)) {
                if (isNew) {
                    OutlinedTextField(value = sid, onValueChange = { sid = it }, label = { Text("编号") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("名称") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("联系人") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("电话") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = fax, onValueChange = { fax = it }, label = { Text("传真") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("邮箱") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("地址") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = website, onValueChange = { website = it }, label = { Text("网址") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("简介") }, modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(10.dp))
            }
        },
        confirmButton = {
            Button(onClick = {
                if (sid.isBlank() || name.isBlank()) return@Button
                val ok = if (isNew) SupplierService.create(Supplier(sid, name, address.ifBlank { null }, website.ifBlank { null }, contact.ifBlank { null }, phone.ifBlank { null }, fax.ifBlank { null }, email.ifBlank { null }, desc.ifBlank { null }))
                else SupplierService.update(supplier.copy(name = name, address = address.ifBlank { null }, website = website.ifBlank { null }, contact = contact.ifBlank { null }, phone = phone.ifBlank { null }, fax = fax.ifBlank { null }, email = email.ifBlank { null }, description = desc.ifBlank { null }))
                if (ok) onSaved()
            }) { Text(if (isNew) "创建" else "保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun DetailRow(label: String, value: String?) {
    if (value != null && value.isNotBlank()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
            Text("$label:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(56.dp))
            Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        }
    }
}
