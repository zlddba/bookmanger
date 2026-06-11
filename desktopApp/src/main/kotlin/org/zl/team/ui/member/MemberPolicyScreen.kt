package org.zl.team.ui.member

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.service.MemberPolicyService
import org.zl.team.entity.MemberPolicy
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun MemberPolicyScreen() {
    var policies by remember { mutableStateOf<List<MemberPolicy>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<MemberPolicy?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            policies = MemberPolicyService.listAll()
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    if (isLoading) {
        LoadingIndicator()
    } else if (loadError != null) {
        ErrorBanner(loadError!!, onRetry = ::load)
    } else if (policies.isEmpty()) {
        EmptyHint("暂无优惠政策")
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("优惠政策", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.weight(1f))
                Button(onClick = { editing = null; showDialog = true }, shape = RoundedCornerShape(10.dp)) { Text("新增政策") }
            }
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(24.dp)) {
                    policies.forEach { p ->
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Text("Lv.${p.level}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(64.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("折扣: ${(p.discount.toDoubleOrNull()?.let { "${(it * 100).toInt()}%" } ?: p.discount)}", fontSize = 13.sp)
                                    Text("最低消费: ${p.minAmount ?: 0} 元", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (!p.gift.isNullOrBlank()) Text("赠品: ${p.gift}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                TextButton(onClick = { editing = p; showDialog = true }) { Text("编辑") }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showDialog) {
        PolicyDialog(editing = editing, onDismiss = { showDialog = false }, onSaved = { showDialog = false; load() })
    }
}

@Composable
private fun PolicyDialog(editing: MemberPolicy?, onDismiss: () -> Unit, onSaved: () -> Unit) {
    val isNew = editing == null
    var level by remember { mutableStateOf(editing?.level?.toString() ?: "") }
    var minAmount by remember { mutableStateOf(editing?.minAmount?.toString() ?: "") }
    var discount by remember { mutableStateOf(editing?.discount ?: "1.0") }
    var gift by remember { mutableStateOf(editing?.gift ?: "") }
    var remark by remember { mutableStateOf(editing?.remark ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "新增政策" else "编辑政策") },
        text = {
            Column(Modifier.widthIn(max = 400.dp)) {
                OutlinedTextField(value = level, onValueChange = { level = it.filter { c -> c.isDigit() } }, label = { Text("等级") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = minAmount, onValueChange = { minAmount = it.filter { c -> c.isDigit() } }, label = { Text("最低消费") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = discount, onValueChange = { discount = it }, label = { Text("折扣(如0.9)") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = gift, onValueChange = { gift = it }, label = { Text("赠品") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = remark, onValueChange = { remark = it }, label = { Text("备注") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            }
        },
        confirmButton = {
            Button(onClick = {
                val levelVal = level.toIntOrNull() ?: return@Button
                val ok = if (isNew) MemberPolicyService.create(MemberPolicy(levelVal, minAmount.toIntOrNull(), discount, gift.ifBlank { null }, remark.ifBlank { null }))
                else MemberPolicyService.update(editing.copy(minAmount = minAmount.toIntOrNull(), discount = discount, gift = gift.ifBlank { null }, remark = remark.ifBlank { null }))
                if (ok) onSaved()
            }) { Text(if (isNew) "创建" else "保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
