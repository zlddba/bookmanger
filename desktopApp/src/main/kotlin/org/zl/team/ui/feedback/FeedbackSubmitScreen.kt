package org.zl.team.ui.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.entity.Feedback
import org.zl.team.service.FeedbackService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun FeedbackSubmitScreen() {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("提交反馈", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(24.dp))
        if (loadError != null) {
            ErrorBanner(loadError!!, onRetry = { loadError = null })
            Spacer(Modifier.height(12.dp))
        }
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.widthIn(max = 440.dp)) {
            Column(Modifier.padding(32.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("姓名") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), enabled = !isLoading)
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("身份") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), enabled = !isLoading)
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("性别") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), enabled = !isLoading)
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("单位") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), enabled = !isLoading)
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("地址") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), enabled = !isLoading)
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("邮箱") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), enabled = !isLoading)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("反馈内容*") }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(10.dp), enabled = !isLoading)
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (content.isBlank()) { message = "反馈内容不能为空"; isSuccess = false; return@Button }
                        isLoading = true
                        loadError = null
                        try {
                            val date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            val ok = FeedbackService.submit(Feedback(0, name.ifBlank { null }, role.ifBlank { null }, gender.ifBlank { null }, company.ifBlank { null }, address.ifBlank { null }, email.ifBlank { null }, content, date))
                            if (ok) { message = "反馈提交成功，感谢您的意见！"; isSuccess = true } else { message = "提交失败"; isSuccess = false }
                        } catch (e: Exception) {
                            loadError = "提交失败: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) { Text(if (isLoading) "提交中..." else "提交反馈", fontSize = 16.sp, fontWeight = FontWeight.Medium) }
                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(message!!, color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        }
    }
}
