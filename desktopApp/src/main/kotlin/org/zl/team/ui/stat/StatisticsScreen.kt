package org.zl.team.ui.stat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.skiaCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.service.StatisticsService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator
import org.zl.team.ui.components.DateField

@Composable
fun StatisticsScreen() {
    var groupBy by remember { mutableStateOf("月") }
    var rows by remember { mutableStateOf(emptyList<StatisticsService.StatRow>()) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var startDate by remember { mutableStateOf(LocalDate.now().minusMonths(12).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var endDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) }
    var selectedTab by remember { mutableStateOf(0) }

    fun query() {
        isLoading = true
        loadError = null
        try {
            rows = StatisticsService.statistics(startDate, endDate, groupBy)
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { query() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("统计分析", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))

        // ─── 查询条件栏 ────────────────────────────────
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("日期: ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            DateField(value = startDate, onValueChange = { startDate = it }, label = "开始日期", modifier = Modifier.width(140.dp))
            Spacer(Modifier.width(8.dp)); Text("~", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(8.dp))
            DateField(value = endDate, onValueChange = { endDate = it }, label = "结束日期", modifier = Modifier.width(140.dp))
            Spacer(Modifier.width(12.dp))
            Button(onClick = { query() }, shape = RoundedCornerShape(10.dp)) { Text("查询") }
            Spacer(Modifier.width(12.dp))
            var groupExpanded by remember { mutableStateOf(false) }
            Box {
                FilterChip(selected = true, onClick = { groupExpanded = true }, label = { Text("按$groupBy", fontSize = 13.sp) })
                DropdownMenu(expanded = groupExpanded, onDismissRequest = { groupExpanded = false }) {
                    listOf("日", "月", "年").forEach { g ->
                        DropdownMenuItem(text = { Text("按$g") }, onClick = { groupBy = g; groupExpanded = false })
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // ─── 汇总卡片 ──────────────────────────────────
        if (!isLoading && loadError == null && rows.isNotEmpty()) {
            val totalPurchase = rows.sumOf { it.purchaseAmount }
            val totalSale = rows.sumOf { it.saleAmount }
            val totalReturn = rows.sumOf { it.returnAmount }
            val totalProfit = rows.sumOf { it.profit }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("进货总额", "¥%.2f".format(totalPurchase), MaterialTheme.colorScheme.error)
                StatCard("销售总额", "¥%.2f".format(totalSale), MaterialTheme.colorScheme.primary)
                StatCard("退货总额", "¥%.2f".format(totalReturn), MaterialTheme.colorScheme.onSurfaceVariant)
                StatCard("总利润", "¥%.2f".format(totalProfit), if (totalProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(12.dp))
        }

        // ─── Tab 切换 ──────────────────────────────────
        PrimaryTabRow(selectedTabIndex = selectedTab, modifier = Modifier.fillMaxWidth()) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("统计表格") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("趋势图表") })
        }

        Spacer(Modifier.height(12.dp))

        Column(Modifier.weight(1f)) {
            when {
                isLoading -> LoadingIndicator()
                loadError != null -> ErrorBanner(loadError!!, onRetry = ::query)
                rows.isEmpty() -> EmptyHint("暂无数据")
                else -> {
                    when (selectedTab) {
                        0 -> StatTable(rows)
                        1 -> StatChart(rows)
                    }
                }
            }
        }
    }
}

// ─── Tab 1: 统计表格 ────────────────────────────────────
@Composable
private fun ColumnScope.StatTable(rows: List<StatisticsService.StatRow>) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxSize()) {
        Column {
            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 12.dp, vertical = 12.dp)) {
                Text("期间", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("进货金额", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("销售金额", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("退货金额", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("利润", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            LazyColumn {
                itemsIndexed(rows) { index, row ->
                    Row(modifier = Modifier.fillMaxWidth().background(
                        if (index % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(row.period, Modifier.weight(0.8f), fontSize = 13.sp)
                        Text("¥%.2f".format(row.purchaseAmount), Modifier.weight(0.8f), fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                        Text("¥%.2f".format(row.saleAmount), Modifier.weight(0.8f), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Text("¥%.2f".format(row.returnAmount), Modifier.weight(0.8f), fontSize = 13.sp)
                        Text("¥%.2f".format(row.profit), Modifier.weight(0.8f), fontSize = 13.sp, fontWeight = FontWeight.Bold,
                            color = if (row.profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                }
            }
        }
    }
}

// ─── Tab 2: 趋势图表 ────────────────────────────────────
@Composable
private fun ColumnScope.StatChart(rows: List<StatisticsService.StatRow>) {
    if (rows.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("暂无数据", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val purchaseColor = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
    val saleColor = MaterialTheme.colorScheme.primary
    val profitColor = MaterialTheme.colorScheme.tertiary

    Column(Modifier.fillMaxSize()) {
        // 图例
        Row(
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = purchaseColor, label = "进货")
            Spacer(Modifier.width(24.dp))
            LegendItem(color = saleColor, label = "销售")
            Spacer(Modifier.width(24.dp))
            LegendItem(color = profitColor, label = "利润")
        }

        // 图表
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            val surfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

            val maxVal = maxOf(
                rows.maxOf { it.purchaseAmount },
                rows.maxOf { it.saleAmount },
                rows.maxOfOrNull { kotlin.math.abs(it.profit) } ?: 0.0
            )
            val yMax = if (maxVal > 0) (maxVal * 1.2) else 1.0

            if (rows.size <= 12) {
                // 数据点较少：等宽柱状图 + 利润折线
                Canvas(modifier = Modifier.fillMaxSize().padding(36.dp, 16.dp, 16.dp, 48.dp)) {
                    val w = size.width / rows.size
                    val h = size.height

                    drawLine(surfaceVariant, Offset(0f, h), Offset(size.width, h), strokeWidth = 1f)
                    for (i in 1..4) {
                        val y = h * i / 5
                        drawLine(surfaceVariant.copy(alpha = 0.15f), Offset(0f, y), Offset(size.width, y), strokeWidth = 0.5f)
                    }

                    rows.forEachIndexed { i, row ->
                        val cx = w * i + w / 2
                        val barW = w * 0.25f

                        val ph = (row.purchaseAmount / yMax * h).toFloat().coerceAtMost(h)
                        drawRect(purchaseColor, Offset(cx - barW, h - ph), Size(barW, ph))

                        val sh = (row.saleAmount / yMax * h).toFloat().coerceAtMost(h)
                        drawRect(saleColor, Offset(cx, h - sh), Size(barW, sh))

                        val py = (h - row.profit / yMax * h).toFloat().coerceIn(0f, h)
                        drawCircle(profitColor, 3f, Offset(cx, py))

                        drawContext.canvas.skiaCanvas.drawString(
                            row.period.takeLast(5), cx - 8f, h + 16f,
                            org.jetbrains.skia.Font().apply { size = 10f },
                            org.jetbrains.skia.Paint().apply { color = surfaceVariant.toArgb() }
                        )
                    }

                    if (rows.size >= 2) {
                        val path = Path()
                        rows.forEachIndexed { i, row ->
                            val cx = w * i + w / 2
                            val py = (h - row.profit / yMax * h).toFloat().coerceIn(0f, h)
                            if (i == 0) path.moveTo(cx, py) else path.lineTo(cx, py)
                        }
                        drawPath(path, profitColor, style = Stroke(width = 2f, cap = StrokeCap.Round))
                    }

                    for (i in 0..4) {
                        val v = yMax * (1.0 - i / 5.0)
                        val y = h * i / 5
                        drawContext.canvas.skiaCanvas.drawString(
                            "¥%.0f".format(v), 0f, y + 4f,
                            org.jetbrains.skia.Font().apply { size = 9f },
                            org.jetbrains.skia.Paint().apply { color = surfaceVariant.toArgb() }
                        )
                    }
                }
            } else {
                // 数据点多：紧凑折线图
                Canvas(modifier = Modifier.fillMaxSize().padding(36.dp, 16.dp, 16.dp, 48.dp)) {
                    val w = size.width / (rows.size - 1).coerceAtLeast(1)
                    val h = size.height

                    drawLine(surfaceVariant, Offset(0f, h), Offset(size.width, h), strokeWidth = 1f)
                    for (i in 1..4) {
                        val y = h * i / 5
                        drawLine(surfaceVariant.copy(alpha = 0.15f), Offset(0f, y), Offset(size.width, y), strokeWidth = 0.5f)
                    }

                    fun drawLinePath(getY: (StatisticsService.StatRow) -> Double, color: Color) {
                        if (rows.size < 2) return
                        val path = Path()
                        rows.forEachIndexed { i, row ->
                            val x = w * i
                            val y = (h - getY(row) / yMax * h).toFloat().coerceIn(0f, h)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(path, color, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
                        rows.forEachIndexed { i, row ->
                            val x = w * i
                            val y = (h - getY(row) / yMax * h).toFloat().coerceIn(0f, h)
                            drawCircle(color, 3f, Offset(x, y))
                        }
                    }

                    drawLinePath({ it.purchaseAmount }, purchaseColor)
                    drawLinePath({ it.saleAmount }, saleColor)
                    drawLinePath({ it.profit }, profitColor)

                    val step = (rows.size / 12).coerceAtLeast(1)
                    rows.forEachIndexed { i, row ->
                        if (i % step == 0 || i == rows.size - 1) {
                            drawContext.canvas.skiaCanvas.drawString(
                                row.period.takeLast(5), w * i - 10f, h + 16f,
                                org.jetbrains.skia.Font().apply { size = 9f },
                                org.jetbrains.skia.Paint().apply { color = surfaceVariant.toArgb() }
                            )
                        }
                    }

                    for (i in 0..4) {
                        val v = yMax * (1.0 - i / 5.0)
                        val y = h * i / 5
                        drawContext.canvas.skiaCanvas.drawString(
                            "¥%.0f".format(v), 0f, y + 4f,
                            org.jetbrains.skia.Font().apply { size = 9f },
                            org.jetbrains.skia.Paint().apply { color = surfaceVariant.toArgb() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.StatCard(label: String, value: String, color: Color) {
    Surface(shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.08f), modifier = Modifier.weight(1f)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun RowScope.LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
