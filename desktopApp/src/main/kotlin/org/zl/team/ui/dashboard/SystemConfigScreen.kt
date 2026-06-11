package org.zl.team.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.service.SystemConfig

@Composable
fun SystemConfigScreen() {
    var alertThreshold by remember { mutableStateOf(SystemConfig.getStockAlertThreshold().toString()) }
    var criticalThreshold by remember { mutableStateOf(SystemConfig.getCriticalStockThreshold().toString()) }
    var retentionCount by remember { mutableStateOf(SystemConfig.getBackupRetentionCount().toString()) }
    var lateFeeRate by remember { mutableStateOf(String.format("%.2f", SystemConfig.getLateFeeDailyRate())) }
    var message by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("系统配置", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(24.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.widthIn(max = 500.dp)) {
            Column(Modifier.padding(32.dp)) {
                Text("库存预警", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("预警阈值", Modifier.width(120.dp), fontSize = 13.sp)
                    OutlinedTextField(value = alertThreshold, onValueChange = { alertThreshold = it.filter { c -> c.isDigit() } },
                        singleLine = true, modifier = Modifier.width(100.dp), shape = RoundedCornerShape(8.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("库存 ≤ 此值时预警", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("严重短缺阈值", Modifier.width(120.dp), fontSize = 13.sp)
                    OutlinedTextField(value = criticalThreshold, onValueChange = { criticalThreshold = it.filter { c -> c.isDigit() } },
                        singleLine = true, modifier = Modifier.width(100.dp), shape = RoundedCornerShape(8.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("红色高亮显示", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                Spacer(Modifier.height(20.dp))

                Text("数据备份", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("备份保留数", Modifier.width(120.dp), fontSize = 13.sp)
                    OutlinedTextField(value = retentionCount, onValueChange = { retentionCount = it.filter { c -> c.isDigit() } },
                        singleLine = true, modifier = Modifier.width(100.dp), shape = RoundedCornerShape(8.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("自动备份最多保留份数", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                Spacer(Modifier.height(20.dp))

                Text("借阅管理", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("每日滞纳金", Modifier.width(120.dp), fontSize = 13.sp)
                    OutlinedTextField(value = lateFeeRate, onValueChange = { lateFeeRate = it },
                        singleLine = true, modifier = Modifier.width(100.dp), shape = RoundedCornerShape(8.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("元/天", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.height(28.dp))

                Button(onClick = {
                    alertThreshold.toIntOrNull()?.let { SystemConfig.setStockAlertThreshold(it) }
                    criticalThreshold.toIntOrNull()?.let { SystemConfig.setCriticalStockThreshold(it) }
                    retentionCount.toIntOrNull()?.let { SystemConfig.setBackupRetentionCount(it) }
                    lateFeeRate.toDoubleOrNull()?.let { SystemConfig.setLateFeeDailyRate(it) }
                    message = "配置已保存"
                }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) { Text("保存配置") }

                if (message != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(message!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
