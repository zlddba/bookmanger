package org.zl.team.ui.admin

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
import org.zl.team.entity.Admin
import org.zl.team.entity.Employee
import org.zl.team.service.EmployeeService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun EmployeeManageScreen() {
    var employees by remember { mutableStateOf<List<Pair<Admin, Employee?>>>(emptyList()) }
    var selectedAccount by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var editingAccount by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            employees = EmployeeService.listAllEmployees()
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        // 左侧：员工列表
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("员工管理", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { editingAccount = null; showDialog = true },
                    shape = RoundedCornerShape(10.dp)
                ) { Text("新建员工") }
            }
            Spacer(Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                when {
                    isLoading -> LoadingIndicator()
                    loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
                    employees.isEmpty() -> EmptyHint("暂无员工数据")
                    else -> {
                        Column {
                            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Text("帐号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("姓名", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("角色", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("性别", Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text("电话", Modifier.weight(1.2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            LazyColumn {
                                itemsIndexed(employees) { index, (admin, emp) ->
                                    val isSelected = admin.userId == selectedAccount
                                    Row(
                                        modifier = Modifier.fillMaxWidth().clickable { selectedAccount = admin.userId }.background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                                index % 2 == 1 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        ).padding(horizontal = 16.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(admin.userId, Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(emp?.name ?: "-", Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1)
                                        Text(admin.role, Modifier.weight(1f), fontSize = 13.sp, maxLines = 1)
                                        Text(emp?.gender ?: "-", Modifier.weight(0.5f), fontSize = 13.sp)
                                        Text(emp?.phone ?: emp?.mobile ?: "-", Modifier.weight(1.2f), fontSize = 13.sp, maxLines = 1)
                                    }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 右侧：操作面板
        if (selectedAccount != null) {
            Spacer(Modifier.width(24.dp))
            val pair = employees.find { it.first.userId == selectedAccount }
            if (pair != null) {
                EmployeeActionPanel(
                    admin = pair.first,
                    employee = pair.second,
                    onEdit = { editingAccount = pair.first.userId; showDialog = true },
                    onRefresh = { load() },
                    onMessage = { message = it }
                )
            }
        }
    }

    // 新建/编辑对话框
    if (showDialog) {
        EmployeeDialog(
            account = editingAccount,
            onDismiss = { showDialog = false },
            onSaved = { showDialog = false; load() }
        )
    }
}

@Composable
private fun EmployeeActionPanel(
    admin: Admin,
    employee: Employee?,
    onEdit: () -> Unit,
    onRefresh: () -> Unit,
    onMessage: (String) -> Unit
) {
    var showResetPwd by remember { mutableStateOf(false) }
    var newPwd by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.width(300.dp).fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text("员工详情", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))

            DetailItem("帐号", admin.userId)
            DetailItem("姓名", employee?.name)
            DetailItem("角色", admin.role)
            DetailItem("性别", employee?.gender)
            DetailItem("地址", employee?.address)
            DetailItem("电话", employee?.phone)
            DetailItem("手机", employee?.mobile)
            DetailItem("邮箱", employee?.email)
            DetailItem("格言", employee?.motto)
            DetailItem("创建日期", employee?.createdAt)

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) { Text("编辑信息") }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = { showResetPwd = !showResetPwd },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) { Text("重置密码") }

            if (showResetPwd) {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPwd,
                    onValueChange = { newPwd = it },
                    label = { Text("新密码") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (newPwd.isNotBlank()) {
                            if (EmployeeService.resetPassword(admin.userId, newPwd)) {
                                onMessage("密码已重置")
                                showResetPwd = false
                                newPwd = ""
                            } else { onMessage("重置失败") }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = newPwd.isNotBlank()
                ) { Text("确认重置") }
            }

            if (admin.userId != "admin") {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        if (EmployeeService.deleteEmployee(admin.userId)) {
                            onMessage("已删除 ${admin.userId}")
                            onRefresh()
                        } else { onMessage("删除失败") }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("删除此员工", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun EmployeeDialog(
    account: String?,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val isNew = account == null
    var userId by remember { mutableStateOf(account ?: "") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("售书员") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var motto by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // 编辑模式加载现有数据
    LaunchedEffect(account) {
        if (account != null) {
            val list = EmployeeService.listAllEmployees()
            val pair = list.find { it.first.userId == account }
            if (pair != null) {
                userId = pair.first.userId
                name = pair.second?.name ?: ""
                role = pair.first.role
                gender = pair.second?.gender ?: ""
                address = pair.second?.address ?: ""
                phone = pair.second?.phone ?: ""
                mobile = pair.second?.mobile ?: ""
                email = pair.second?.email ?: ""
                motto = pair.second?.motto ?: ""
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "新建员工" else "编辑员工") },
        text = {
            Column(modifier = Modifier.widthIn(max = 400.dp)) {
                if (isNew) {
                    OutlinedTextField(value = userId, onValueChange = { userId = it; error = null }, label = { Text("帐号") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it; error = null }, label = { Text("密码") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("姓名") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("性别") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("电话") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("地址") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("手机") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("邮箱") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = motto, onValueChange = { motto = it }, label = { Text("格言") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))

                if (isNew) {
                    Spacer(Modifier.height(8.dp))
                    // 角色选择
                    Text("角色", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    val roles = listOf("售书员", "仓库管理员")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        roles.forEach { r ->
                            FilterChip(
                                selected = role == r,
                                onClick = { role = r },
                                label = { Text(r, fontSize = 13.sp) }
                            )
                        }
                    }
                }

                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (isNew && (userId.isBlank() || password.isBlank())) {
                    error = "帐号和密码不能为空"; return@Button
                }
                if (name.isBlank()) { error = "姓名不能为空"; return@Button }
                val ok = if (isNew) {
                    EmployeeService.createEmployee(userId, name, password, gender.ifBlank { null }, address.ifBlank { null }, phone.ifBlank { null }, mobile.ifBlank { null }, email.ifBlank { null }, motto.ifBlank { null }, role)
                } else {
                    EmployeeService.saveEmployee(userId, name, gender.ifBlank { null }, address.ifBlank { null }, phone.ifBlank { null }, mobile.ifBlank { null }, email.ifBlank { null }, motto.ifBlank { null })
                }
                if (ok) onSaved() else { error = "操作失败，帐号可能已存在" }
            }) { Text(if (isNew) "创建" else "保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
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
