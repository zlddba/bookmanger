package org.zl.team.ui.dashboard

import androidx.compose.foundation.background
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
import org.zl.team.service.BackupService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import java.text.DecimalFormat

@Composable
fun BackupRestoreScreen() {
    var backups by remember { mutableStateOf<List<BackupService.BackupInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var showConfirmRestore by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            backups = BackupService.listBackups()
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("数据备份与恢复", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        // ─── 操作按钮区 ────────────────────────────────
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                try {
                    val path = BackupService.createBackup()
                    message = "备份成功: $path"; isSuccess = true; load()
                } catch (e: Exception) {
                    message = "备份失败: ${e.message}"; isSuccess = false
                }
            }, shape = RoundedCornerShape(10.dp)) { Text("创建备份") }

            OutlinedButton(onClick = { load() }, shape = RoundedCornerShape(10.dp)) { Text("刷新列表") }

            Spacer(Modifier.weight(1f))
            Text("备份目录: ${BackupService.BackupInfo::class.java.protectionDomain?.codeSource?.location?.path ?: ""}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (message != null) {
            Spacer(Modifier.height(8.dp))
            Surface(shape = RoundedCornerShape(8.dp), color = (if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error).copy(alpha = 0.12f)) {
                Text(message!!, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 13.sp,
                    color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
        }

        Spacer(Modifier.height(12.dp))

        // ─── 备份列表 ──────────────────────────────────
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
            when {
                isLoading -> LoadingIndicator()
                loadError != null -> ErrorBanner(loadError!!, onRetry = ::load)
                backups.isEmpty() -> EmptyHint("暂无备份文件")
                else -> {
                    Column {
                        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("文件名", Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("大小", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("创建时间", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("操作", Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        LazyColumn {
                            itemsIndexed(backups) { index, backup ->
                                val df = DecimalFormat("#.##")
                                val sizeStr = when {
                                    backup.fileSize < 1024 -> "${backup.fileSize} B"
                                    backup.fileSize < 1024 * 1024 -> "${df.format(backup.fileSize / 1024.0)} KB"
                                    else -> "${df.format(backup.fileSize / (1024.0 * 1024.0))} MB"
                                }
                                Row(modifier = Modifier.fillMaxWidth().background(
                                    if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                                ).padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(backup.fileName, Modifier.weight(2f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(sizeStr, Modifier.weight(0.8f), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(backup.createdAt, Modifier.weight(1f), fontSize = 13.sp)
                                    Row(Modifier.weight(1f)) {
                                        TextButton(onClick = { showConfirmRestore = backup.fileName }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                                            Text("恢复", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                        }
                                        TextButton(onClick = {
                                            BackupService.deleteBackup(backup.fileName)
                                            load()
                                        }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                                            Text("删除", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                            }
                        }
                    }
                }
            }
        }
    }

    // ─── 恢复确认对话框 ──────────────────────────────
    if (showConfirmRestore != null) {
        AlertDialog(
            onDismissRequest = { showConfirmRestore = null },
            title = { Text("确认恢复") },
            text = { Text("将使用备份文件「${showConfirmRestore}」覆盖当前数据库。\n此操作不可撤销，建议先创建新备份。\n恢复后需重新登录。") },
            confirmButton = {
                Button(onClick = {
                    try {
                        BackupService.restoreBackup(showConfirmRestore!!)
                        message = "恢复成功，请重新登录"; isSuccess = true
                    } catch (e: Exception) {
                        message = "恢复失败: ${e.message}"; isSuccess = false
                    }
                    showConfirmRestore = null
                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("确认恢复") }
            },
            dismissButton = { TextButton(onClick = { showConfirmRestore = null }) { Text("取消") } }
        )
    }
}
