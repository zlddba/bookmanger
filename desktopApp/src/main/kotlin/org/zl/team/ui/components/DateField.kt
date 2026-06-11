package org.zl.team.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Composable
fun DateField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "日期",
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    Box(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newVal ->
                // 允许手动输入，但限制为日期格式
                if (newVal.length <= 10 && newVal.all { it.isDigit() || it == '-' }) {
                    onValueChange(newVal)
                }
            },
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                TextButton(onClick = { showPicker = true }) {
                    Text("📅", fontSize = 14.sp)
                }
            }
        )

        if (showPicker) {
            DatePickerDialog(
                initialDate = parseDate(value),
                onConfirm = { date ->
                    onValueChange(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                    showPicker = false
                },
                onDismiss = { showPicker = false }
            )
        }
    }
}

@Composable
private fun DatePickerDialog(
    initialDate: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var currentMonth by remember { mutableStateOf(initialDate.withDayOfMonth(1)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Text("<", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    currentMonth.format(DateTimeFormatter.ofPattern("yyyy 年 M 月")),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Text(">", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 星期表头
                Row(Modifier.fillMaxWidth()) {
                    listOf("一", "二", "三", "四", "五", "六", "日").forEach { day ->
                        Text(
                            day, modifier = Modifier.weight(1f),
                            fontSize = 12.sp, textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))

                // 日历网格
                val firstDayOfWeek = currentMonth.dayOfWeek.value - 1 // 1=周一...7=周日
                val daysInMonth = currentMonth.lengthOfMonth()
                val today = LocalDate.now()

                var day = 1
                val rows = ((firstDayOfWeek + daysInMonth + 6) / 7).coerceAtLeast(1)
                for (row in 0 until rows) {
                    Row(Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            val cell = row * 7 + col
                            if (cell < firstDayOfWeek || day > daysInMonth) {
                                Box(Modifier.weight(1f).aspectRatio(1f))
                            } else {
                                val date = currentMonth.withDayOfMonth(day)
                                val isSelected = date == selectedDate
                                val isToday = date == today

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        )
                                        .clickable { selectedDate = date },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$day",
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                day++
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedDate) }) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

private fun parseDate(value: String): LocalDate {
    return try {
        LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    } catch (_: Exception) {
        LocalDate.now()
    }
}
