package org.zl.team.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.service.EmployeeService
import org.zl.team.util.SessionManager
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun ChangePasswordScreen() {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPwd by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    when {
        isLoading -> LoadingIndicator()
        loadError != null -> ErrorBanner(loadError!!, onRetry = { loadError = null })
        else -> {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("修改密码", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.height(24.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.widthIn(max = 360.dp)) {
                    Column(Modifier.padding(32.dp)) {
                        OutlinedTextField(value = oldPassword, onValueChange = { oldPassword = it; message = null }, label = { Text("原密码") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = newPassword, onValueChange = { newPassword = it; message = null }, label = { Text("新密码") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = confirmPwd, onValueChange = { confirmPwd = it; message = null }, label = { Text("确认新密码") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = {
                                when {
                                    oldPassword.isBlank() || newPassword.isBlank() -> { message = "请填写完整"; isSuccess = false }
                                    newPassword != confirmPwd -> { message = "两次密码不一致"; isSuccess = false }
                                    else -> {
                                        isLoading = true
                                        loadError = null
                                        try {
                                            val ok = EmployeeService.changePassword(SessionManager.currentUserId, oldPassword, newPassword)
                                            if (ok) { message = "密码已修改"; isSuccess = true }
                                            else { message = "修改失败，原密码错误"; isSuccess = false }
                                        } catch (e: Exception) {
                                            loadError = "修改失败: ${e.message}"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp)
                        ) { Text("确认修改", fontSize = 16.sp) }
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
