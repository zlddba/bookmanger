package org.zl.team.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.service.LoginService
import org.zl.team.util.SessionManager

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loginAttempts by remember { mutableIntStateOf(0) }

    fun doLogin() {
        if (loginAttempts >= 3) {
            errorMessage = "登录失败超过3次，请联系管理员"
            return
        }
        val role = LoginService.verify(userId, password)
        if (role != null) {
            SessionManager.login(userId, role)
            errorMessage = null
            loginAttempts = 0
            onLoginSuccess()
        } else {
            loginAttempts++
            errorMessage = "用户ID或密码错误（${loginAttempts}/3）"
        }
    }

    fun enterAsGuest() {
        SessionManager.login("游客", "游客")
        onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.widthIn(min = 360.dp, max = 400.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 应用图标区
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LS",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "图书管理系统",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "内电子信息学院",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it; errorMessage = null },
                    label = { Text("用户ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = loginAttempts < 3,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("密码") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        if (userId.isNotBlank() && loginAttempts < 3) doLogin()
                    }),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = loginAttempts < 3,
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Spacer(Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = errorMessage!!,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { doLogin() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = loginAttempts < 3 && userId.isNotBlank(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("登  录", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(8.dp))

                TextButton(
                    onClick = { enterAsGuest() },
                    enabled = loginAttempts < 3
                ) {
                    Text(
                        "游客进入",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
