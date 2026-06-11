package org.zl.team.ui.member

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.Member
import org.zl.team.service.MemberService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun NewMemberScreen() {
    var cardNo by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var motto by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    when {
        isLoading -> LoadingIndicator()
        loadError != null -> ErrorBanner(loadError!!, onRetry = { loadError = null })
        else -> {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text("新建会员", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(24.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.widthIn(max = 440.dp)) {
                    Column(Modifier.padding(32.dp)) {
                        OutlinedTextField(value = cardNo, onValueChange = { cardNo = it; message = null }, label = { Text("卡号*") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = password, onValueChange = { password = it; message = null }, label = { Text("密码*") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("姓名*") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
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
                            OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("单位") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("邮箱") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = motto, onValueChange = { motto = it }, label = { Text("格言") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = {
                                if (cardNo.isBlank() || password.isBlank() || name.isBlank()) {
                                    message = "卡号、密码和姓名为必填项"; isSuccess = false; return@Button
                                }
                                isLoading = true
                                loadError = null
                                try {
                                    val ok = MemberService.create(Member(cardNo, 1, name, gender.ifBlank { null }, address.ifBlank { null }, company.ifBlank { null }, phone.ifBlank { null }, email.ifBlank { null }, motto.ifBlank { null }, null), password)
                                    if (ok) { message = "会员创建成功"; isSuccess = true } else { message = "创建失败，卡号可能已存在"; isSuccess = false }
                                } catch (e: Exception) {
                                    loadError = "创建失败: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp)
                        ) { Text("创建会员", fontSize = 16.sp, fontWeight = FontWeight.Medium) }
                        if (message != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(message!!, color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
