package org.zl.team.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (totalPages <= 1) return

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 0,
            contentPadding = PaddingValues(horizontal = 8.dp),
            shape = RoundedCornerShape(6.dp)
        ) { Text("◀", fontSize = 11.sp) }

        Spacer(Modifier.width(4.dp))

        val range = (maxOf(0, currentPage - 2)..minOf(totalPages - 1, currentPage + 2))
        range.forEach { i ->
            if (i == currentPage) {
                Button(
                    onClick = { onPageChange(i) },
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(6.dp)
                ) { Text("${i + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            } else {
                TextButton(
                    onClick = { onPageChange(i) },
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(6.dp)
                ) { Text("${i + 1}", fontSize = 12.sp) }
            }
            Spacer(Modifier.width(2.dp))
        }

        TextButton(
            onClick = { onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages - 1,
            contentPadding = PaddingValues(horizontal = 8.dp),
            shape = RoundedCornerShape(6.dp)
        ) { Text("▶", fontSize = 11.sp) }
    }
}

fun <T> List<T>.page(page: Int, pageSize: Int): List<T> =
    drop(page * pageSize).take(pageSize)
