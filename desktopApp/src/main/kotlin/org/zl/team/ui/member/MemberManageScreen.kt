package org.zl.team.ui.member

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
import org.zl.team.entity.Member
import org.zl.team.service.MemberService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun MemberManageScreen() {
    var members by remember { mutableStateOf<List<Member>>(emptyList()) }
    var selected by remember { mutableStateOf<Member?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Member?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            members = MemberService.listAll()
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
                Text("会员管理", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.weight(1f))
                Button(onClick = { editing = null; showDialog = true }, shape = RoundedCornerShape(10.dp)) {
                    Text("新建会员")
                }
            }
            Spacer(Modifier.height(16.dp))
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    isLoading -> LoadingIndicator()
                    loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
                    members.isEmpty() -> EmptyHint("暂无会员数据")
                    else -> {
                        Column {
                            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Text("卡号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("姓名", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("等级", Modifier.weight(0.4f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("电话", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("累计消费", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("注册日期", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            LazyColumn {
                                itemsIndexed(members) { index, m ->
                                    val isSel = selected?.cardNo == m.cardNo
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { selected = m }.background(
                                            when { isSel -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f); index % 2 == 1 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f); else -> MaterialTheme.colorScheme.surface }
                                        ).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(m.cardNo, Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(m.name, Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1)
                                        Text("Lv.${m.level}", Modifier.weight(0.4f), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                        Text(m.phone ?: "-", Modifier.weight(1f), fontSize = 13.sp)
                                        Text("¥${"%.2f".format(m.totalSpent)}", Modifier.weight(0.8f), fontSize = 13.sp)
                                        Text(m.regDate ?: "-", Modifier.weight(0.8f), fontSize = 13.sp)
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                                }
                            }
                        }
                    }
                }
            }
        }
        if (selected != null) {
            Spacer(Modifier.width(24.dp))
            MemberDetailPanel(
                member = selected!!,
                onEdit = { editing = selected; showDialog = true },
                onDelete = { showDeleteConfirm = true }
            )
        }
    }
    if (showDialog) {
        MemberDialog(member = editing, onDismiss = { showDialog = false }, onSaved = { showDialog = false; load() })
    }

    if (showDeleteConfirm && selected != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除会员「${selected!!.cardNo}」吗？此操作不可撤销。") },
            confirmButton = {
                Button(onClick = {
                    MemberService.delete(selected!!.cardNo)
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
private fun MemberDetailPanel(member: Member, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(modifier = Modifier.width(300.dp).fillMaxHeight(), shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text("会员详情", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            ItemRow("卡号", member.cardNo)
            ItemRow("姓名", member.name)
            ItemRow("等级", "Lv.${member.level}")
            ItemRow("性别", member.gender)
            ItemRow("地址", member.address)
            ItemRow("单位", member.company)
            ItemRow("电话", member.phone)
            ItemRow("邮箱", member.email)
            ItemRow("格言", member.motto)
            ItemRow("注册日期", member.regDate)
            ItemRow("累计消费", "¥%.2f".format(member.totalSpent))
            Spacer(Modifier.weight(1f))
            Button(onClick = onEdit, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) { Text("编辑") }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) { Text("删除", color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun MemberDialog(member: Member?, onDismiss: () -> Unit, onSaved: () -> Unit) {
    val isNew = member == null
    var cardNo by remember { mutableStateOf(member?.cardNo ?: "") }
    var name by remember { mutableStateOf(member?.name ?: "") }
    var gender by remember { mutableStateOf(member?.gender ?: "") }
    var address by remember { mutableStateOf(member?.address ?: "") }
    var company by remember { mutableStateOf(member?.company ?: "") }
    var phone by remember { mutableStateOf(member?.phone ?: "") }
    var email by remember { mutableStateOf(member?.email ?: "") }
    var motto by remember { mutableStateOf(member?.motto ?: "") }
    var level by remember { mutableStateOf(member?.level?.toString() ?: "1") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "新建会员" else "编辑会员") },
        text = {
            Column(Modifier.widthIn(max = 400.dp)) {
                if (isNew) {
                    OutlinedTextField(value = cardNo, onValueChange = { cardNo = it }, label = { Text("卡号") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("密码") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.height(8.dp))
                }
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("姓名") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("性别") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("电话") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("邮箱") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("地址") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("单位") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = level, onValueChange = { level = it.filter { c -> c.isDigit() } }, label = { Text("等级") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = motto, onValueChange = { motto = it }, label = { Text("格言") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            }
        },
        confirmButton = {
            Button(onClick = {
                if ((isNew && (cardNo.isBlank() || password.isBlank())) || name.isBlank()) return@Button
                val levelVal = level.toIntOrNull() ?: 1
                val ok = if (isNew) MemberService.create(Member(cardNo, levelVal, name, gender.ifBlank { null }, address.ifBlank { null }, company.ifBlank { null }, phone.ifBlank { null }, email.ifBlank { null }, motto.ifBlank { null }, null), password)
                else MemberService.update(member.copy(name = name, level = levelVal, gender = gender.ifBlank { null }, address = address.ifBlank { null }, company = company.ifBlank { null }, phone = phone.ifBlank { null }, email = email.ifBlank { null }, motto = motto.ifBlank { null }))
                if (ok) onSaved()
            }) { Text(if (isNew) "创建" else "保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun ItemRow(label: String, value: String?) {
    if (value != null && value.isNotBlank()) {
        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
            Text("$label:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(56.dp))
            Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        }
    }
}
