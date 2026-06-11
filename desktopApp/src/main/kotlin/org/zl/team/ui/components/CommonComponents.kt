package org.zl.team.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("加载中...", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ErrorBanner(message: String, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
            modifier = Modifier.widthIn(max = 400.dp)
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("出错了", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Text(message, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                if (onRetry != null) {
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry, shape = RoundedCornerShape(10.dp)) { Text("重试") }
                }
            }
        }
    }
}

@Composable
fun EmptyHint(text: String = "暂无数据", modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📂", fontSize = 32.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(8.dp))
            Text(text, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    activeColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    warningColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.tertiary,
    errorColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.error,
    mutedColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val color = when (text.lowercase()) {
        "已归还", "已完成", "已付款" -> activeColor
        "借阅中", "处理中", "待处理" -> warningColor
        "已逾期", "库存不足", "失败" -> errorColor
        else -> mutedColor
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
fun HighlightedText(
    text: String,
    keyword: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    normalColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    highlightColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    if (keyword.isBlank() || !text.contains(keyword, ignoreCase = true)) {
        Text(text, modifier = modifier, fontSize = 13.sp, maxLines = maxLines, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, color = normalColor)
    } else {
        val lowerText = text.lowercase()
        val lowerKeyword = keyword.lowercase()
        var start = 0
        Text(
            buildAnnotatedString {
                while (true) {
                    val idx = lowerText.indexOf(lowerKeyword, startIndex = start)
                    if (idx < 0) {
                        append(text.substring(start))
                        break
                    }
                    append(text.substring(start, idx))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = highlightColor, background = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))) {
                        append(text.substring(idx, idx + keyword.length))
                    }
                    start = idx + keyword.length
                }
            },
            modifier = modifier, fontSize = 13.sp, maxLines = maxLines, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}
