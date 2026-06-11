package org.zl.team.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import org.zl.team.entity.BookCategory
import org.zl.team.service.CategoryService
import org.zl.team.ui.components.EmptyHint
import org.zl.team.ui.components.ErrorBanner
import org.zl.team.ui.components.LoadingIndicator

@Composable
fun CategoryManageScreen() {
    var categories by remember { mutableStateOf<List<BookCategory>>(emptyList()) }
    var selected by remember { mutableStateOf<BookCategory?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<BookCategory?>(null) }
    var parentId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }

    fun load() {
        isLoading = true
        loadError = null
        try {
            categories = CategoryService.listAll()
        } catch (e: Exception) {
            loadError = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    LaunchedEffect(Unit) { load() }

    val hasActiveFilter = selected != null
    if (isLoading) {
        LoadingIndicator()
    } else if (loadError != null) {
        ErrorBanner(loadError!!, onRetry = ::load)
    } else if (categories.isEmpty() && !hasActiveFilter) {
        EmptyHint("暂无分类数据")
    } else {
        Row(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("图书分类", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.weight(1f))
                    if (selected != null) {
                        Text("父分类: ${selected!!.name}(${selected!!.categoryId})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { parentId = null; selected = null }, shape = RoundedCornerShape(10.dp)) {
                            Text("全部")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { editing = null; parentId = selected!!.categoryId; showDialog = true }, shape = RoundedCornerShape(10.dp)) {
                            Text("新增子分类")
                        }
                    }
                    Button(onClick = { editing = null; parentId = null; showDialog = true }, shape = RoundedCornerShape(10.dp)) {
                        Text("新增顶级分类")
                    }
                }
                Spacer(Modifier.height(16.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth().weight(1f)) {
                    Column {
                        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).padding(horizontal = 16.dp, vertical = 12.dp)) {
                            Text("编号", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("名称", Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("父分类", Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("子分类数", Modifier.weight(0.8f), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        val displayList = if (selected != null) categories.filter { it.parentId == selected!!.categoryId } else categories.filter { it.parentId == null }
                        LazyColumn {
                            itemsIndexed(displayList) { index, c ->
                                val isSel = selected?.categoryId == c.categoryId
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable { selected = c }.background(
                                        when { isSel -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f); index % 2 == 1 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f); else -> MaterialTheme.colorScheme.surface }
                                    ).padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(c.categoryId, Modifier.weight(0.8f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(c.name, Modifier.weight(1.5f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(c.parentId ?: "(顶级)", Modifier.weight(1.5f), fontSize = 13.sp)
                                    val childCount = categories.count { it.parentId == c.categoryId }
                                    Text("$childCount", Modifier.weight(0.8f), fontSize = 13.sp, color = if (childCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.15f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CategoryDialog(
            editing = editing,
            parentId = parentId,
            categories = categories,
            onDismiss = { showDialog = false },
            onSaved = { showDialog = false; load() }
        )
    }
}

@Composable
private fun CategoryDialog(
    editing: BookCategory?,
    parentId: String?,
    categories: List<BookCategory>,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    val isNew = editing == null
    var cid by remember { mutableStateOf(editing?.categoryId ?: "") }
    var name by remember { mutableStateOf(editing?.name ?: "") }
    var selectedParentId by remember { mutableStateOf(editing?.parentId ?: parentId ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "新增分类" else "编辑分类") },
        text = {
            Column(Modifier.widthIn(max = 400.dp)) {
                if (isNew) {
                    OutlinedTextField(value = cid, onValueChange = { cid = it }, label = { Text("编号") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("名称") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Spacer(Modifier.height(8.dp))
                Text("父分类", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                val parentOptions = listOf(BookCategory("", "(无 - 顶级分类)", null) to "") + categories.map { it to it.categoryId }
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = parentOptions.find { it.second == selectedParentId }?.first?.name ?: "(无 - 顶级分类)",
                        onValueChange = {},
                        readOnly = true, singleLine = true,
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = { Text(if (expanded) "▾" else "▸", fontSize = 10.sp) }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.widthIn(max = 380.dp)) {
                        parentOptions.forEach { (cat, id) ->
                            DropdownMenuItem(
                                text = { Text(cat.name, fontSize = 13.sp) },
                                onClick = { selectedParentId = id; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (cid.isBlank() || name.isBlank()) return@Button
                val ok = if (isNew) CategoryService.create(BookCategory(cid, name, selectedParentId.ifBlank { null }))
                else CategoryService.update(editing.copy(name = name, parentId = selectedParentId.ifBlank { null }))
                if (ok) onSaved()
            }) { Text(if (isNew) "创建" else "保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}
