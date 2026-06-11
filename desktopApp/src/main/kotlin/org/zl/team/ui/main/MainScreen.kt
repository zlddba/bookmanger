package org.zl.team.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.zl.team.ui.admin.EmployeeManageScreen
import org.zl.team.ui.book.BookManageScreen
import org.zl.team.ui.book.BookstoreInfoScreen
import org.zl.team.ui.book.CategoryManageScreen
import org.zl.team.ui.feedback.FeedbackManageScreen
import org.zl.team.ui.feedback.FeedbackSubmitScreen
import org.zl.team.ui.finance.GrossProfitScreen
import org.zl.team.ui.finance.SettlementScreen
import org.zl.team.ui.login.ChangePasswordScreen
import org.zl.team.ui.member.MemberBorrowHistoryScreen
import org.zl.team.ui.member.MemberPurchaseHistoryScreen
import org.zl.team.ui.member.MemberPolicyViewScreen
import org.zl.team.ui.member.MemberManageScreen
import org.zl.team.ui.member.MemberPolicyScreen
import org.zl.team.ui.member.MemberSelfScreen
import org.zl.team.ui.member.NewMemberScreen
import org.zl.team.ui.purchase.PurchaseQueryScreen
import org.zl.team.ui.purchase.PurchaseRegisterScreen
import org.zl.team.ui.purchase.SupplierManageScreen
import org.zl.team.ui.returns.ReturnQueryScreen
import org.zl.team.ui.returns.ReturnRegisterScreen
import org.zl.team.ui.returns.ReturnStatScreen
import org.zl.team.ui.sale.MemberPurchaseLookupScreen
import org.zl.team.ui.sale.SaleQueryScreen
import org.zl.team.ui.sale.TodayStatScreen
import org.zl.team.ui.stock.BorrowManageScreen
import org.zl.team.service.BorrowService
import org.zl.team.ui.stock.ReservationScreen
import org.zl.team.ui.stock.StockStatScreen
import org.zl.team.ui.sale.SellScreen
import org.zl.team.ui.search.BookSearchScreen
import org.zl.team.ui.stat.StatisticsScreen
import org.zl.team.ui.ThemeManager
import org.zl.team.util.SessionManager

// ─── 数据结构 ────────────────────────────────────────────
data class MenuItem(val label: String, val screen: Screen)
data class MenuGroup(val label: String, val items: List<MenuItem>, val icon: String = "")

// ─── 主界面 ──────────────────────────────────────────────
@Composable
fun MainScreen(onLogout: () -> Unit) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.BookSearch) }

    val role = SessionManager.currentUserRole
    val menus = remember(role) { getMenuGroups(role) }

    // 根据当前角色和菜单，找到当前屏所属组的展开状态
    var expandedGroups by remember { mutableStateOf(setOf<String>()) }

    // 初始化展开第一个有项的分组
    LaunchedEffect(menus) {
        if (expandedGroups.isEmpty() && menus.isNotEmpty()) {
            expandedGroups = setOf(menus.first().label)
        }
    }

    var overdueCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { overdueCount = BorrowService.getOverdueCount() }

    Row(modifier = Modifier.fillMaxSize()) {
        // ─── 侧边栏 ──────────────────────────────────────
        NavigationSidebar(
            menus = menus,
            currentScreen = currentScreen,
            expandedGroups = expandedGroups,
            overdueCount = overdueCount,
            onToggleGroup = { label ->
                expandedGroups = if (label in expandedGroups)
                    expandedGroups - label
                else
                    expandedGroups + label
            },
            onScreenSelected = { currentScreen = it },
            onLogout = onLogout
        )

        // ─── 内容区 ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            ContentArea(currentScreen)
        }
    }
}

// ─── 侧边栏 ──────────────────────────────────────────────
@Composable
private fun NavigationSidebar(
    menus: List<MenuGroup>,
    currentScreen: Screen,
    expandedGroups: Set<String>,
    overdueCount: Int,
    onToggleGroup: (String) -> Unit,
    onScreenSelected: (Screen) -> Unit,
    onLogout: () -> Unit
) {
    Surface(
        modifier = Modifier.width(220.dp).fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部标识
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "LS",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "图书管理系统",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // 导航菜单
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                menus.forEach { group ->
                    NavigationGroup(
                        group = group,
                        isExpanded = group.label in expandedGroups,
                        currentScreen = currentScreen,
                        overdueCount = overdueCount,
                        onToggle = { onToggleGroup(group.label) },
                        onSelect = onScreenSelected
                    )
                }
            }

            // 底部用户信息
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        SessionManager.currentUserName,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        SessionManager.currentUserRole,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick = onLogout,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("退出登录", fontSize = 12.sp)
                        }
                        TextButton(
                            onClick = { ThemeManager.toggle() },
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(if (ThemeManager.isDarkMode) "☀" else "☾", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// ─── 导航组 ──────────────────────────────────────────────
@Composable
private fun NavigationGroup(
    group: MenuGroup,
    isExpanded: Boolean,
    currentScreen: Screen,
    overdueCount: Int,
    onToggle: () -> Unit,
    onSelect: (Screen) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 分组标题
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onToggle),
            color = if (isExpanded)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (group.icon.isNotBlank()) {
                    Text(group.icon, fontSize = 14.sp)
                    Spacer(Modifier.width(6.dp))
                }
                Text(
                    group.label,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.weight(1f))
                Text(
                    if (isExpanded) "▾" else "▸",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 子菜单项
        if (isExpanded) {
            group.items.forEach { item ->
                val isActive = currentScreen == item.screen
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onSelect(item.screen) },
                    color = if (isActive)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else
                        MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            item.label,
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                            color = if (isActive)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        if (item.screen == Screen.BorrowManage && overdueCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.error
                            ) {
                                Text(
                                    "$overdueCount",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── 内容区 ──────────────────────────────────────────────
@Composable
private fun ContentArea(currentScreen: Screen) {
    when (currentScreen) {
        is Screen.BookSearch -> BookSearchScreen()
        is Screen.Sell -> SellScreen()
        is Screen.EmployeeManage -> EmployeeManageScreen()
        is Screen.SupplierManage -> SupplierManageScreen()
        is Screen.BookManage -> BookManageScreen()
        is Screen.CategoryManage -> CategoryManageScreen()
        is Screen.BookstoreInfo -> BookstoreInfoScreen()
        is Screen.PurchaseRegister -> PurchaseRegisterScreen()
        is Screen.PurchaseQuery -> PurchaseQueryScreen()
        is Screen.ReturnRegister -> ReturnRegisterScreen()
        is Screen.ReturnQuery -> ReturnQueryScreen()
        is Screen.ReturnStat -> ReturnStatScreen()
        is Screen.StockStat -> StockStatScreen()
        is Screen.Reservation -> ReservationScreen()
        is Screen.ChangePassword -> ChangePasswordScreen()
        is Screen.Statistics -> StatisticsScreen()
        is Screen.TodayStat -> TodayStatScreen()
        is Screen.SaleQuery -> SaleQueryScreen()
        is Screen.MemberManage -> MemberManageScreen()
        is Screen.NewMember -> NewMemberScreen()
        is Screen.MemberPolicy -> MemberPolicyScreen()
        is Screen.MemberSelf -> MemberSelfScreen()
        is Screen.MemberBorrowHistory -> MemberBorrowHistoryScreen()
        is Screen.MemberPurchaseHistory -> MemberPurchaseHistoryScreen()
        is Screen.MemberPolicyView -> MemberPolicyViewScreen()
        is Screen.MemberPurchaseLookup -> MemberPurchaseLookupScreen()
        is Screen.FeedbackSubmit -> FeedbackSubmitScreen()
        is Screen.FeedbackManage -> FeedbackManageScreen()
        is Screen.Settlement -> SettlementScreen()
        is Screen.GrossProfit -> GrossProfitScreen()
        is Screen.BorrowManage -> BorrowManageScreen()
        is Screen.Logout -> Unit
    }
}

// ─── 菜单定义 ────────────────────────────────────────────
fun getMenuGroups(role: String): List<MenuGroup> = when (role) {
    "经理" -> listOf(
        MenuGroup("系统", listOf(
            MenuItem("修改密码", Screen.ChangePassword)
        )),
        MenuGroup("员工管理", listOf(
            MenuItem("员工管理", Screen.EmployeeManage)
        )),
        MenuGroup("图书管理", listOf(
            MenuItem("图书资料", Screen.BookManage),
            MenuItem("图书分类", Screen.CategoryManage),
            MenuItem("书店简介", Screen.BookstoreInfo)
        )),
        MenuGroup("进货管理", listOf(
            MenuItem("新书入库", Screen.PurchaseRegister),
            MenuItem("进货查询", Screen.PurchaseQuery),
            MenuItem("供应商管理", Screen.SupplierManage)
        )),
        MenuGroup("退货管理", listOf(
            MenuItem("退货登记", Screen.ReturnRegister),
            MenuItem("退货查询", Screen.ReturnQuery),
            MenuItem("退货统计", Screen.ReturnStat)
        )),
        MenuGroup("库存管理", listOf(
            MenuItem("库存统计", Screen.StockStat),
            MenuItem("图书预订", Screen.Reservation)
        )),
        MenuGroup("销售管理", listOf(
            MenuItem("图书销售", Screen.Sell),
            MenuItem("今日统计", Screen.TodayStat),
            MenuItem("销售查询", Screen.SaleQuery)
        )),
        MenuGroup("会员管理", listOf(
            MenuItem("会员列表", Screen.MemberManage),
            MenuItem("新建会员", Screen.NewMember),
            MenuItem("优惠政策", Screen.MemberPolicy)
        )),
        MenuGroup("图书检索", listOf(MenuItem("图书检索", Screen.BookSearch))),
        MenuGroup("借阅管理", listOf(MenuItem("借阅管理", Screen.BorrowManage))),
        MenuGroup("统计分析", listOf(MenuItem("统计分析", Screen.Statistics))),
        MenuGroup("客户反馈", listOf(MenuItem("反馈管理", Screen.FeedbackManage))),
        MenuGroup("财务管理", listOf(
            MenuItem("结算对账", Screen.Settlement),
            MenuItem("毛利统计", Screen.GrossProfit)
        )),
    )

    "仓库管理员" -> listOf(
        MenuGroup("系统", listOf(MenuItem("修改密码", Screen.ChangePassword))),
        MenuGroup("图书管理", listOf(
            MenuItem("图书资料", Screen.BookManage)
        )),
        MenuGroup("进货管理", listOf(
            MenuItem("新书入库", Screen.PurchaseRegister),
            MenuItem("进货查询", Screen.PurchaseQuery),
            MenuItem("供应商管理", Screen.SupplierManage)
        )),
        MenuGroup("退货管理", listOf(
            MenuItem("退货登记", Screen.ReturnRegister),
            MenuItem("退货查询", Screen.ReturnQuery),
            MenuItem("退货统计", Screen.ReturnStat)
        )),
        MenuGroup("库存管理", listOf(
            MenuItem("库存统计", Screen.StockStat),
            MenuItem("图书预订", Screen.Reservation)
        )),
        MenuGroup("借阅管理", listOf(MenuItem("借阅管理", Screen.BorrowManage))),
        MenuGroup("图书检索", listOf(MenuItem("图书检索", Screen.BookSearch))),
    )

    "售书员" -> listOf(
        MenuGroup("系统", listOf(MenuItem("修改密码", Screen.ChangePassword))),
        MenuGroup("销售管理", listOf(
            MenuItem("图书销售", Screen.Sell),
            MenuItem("今日统计", Screen.TodayStat),
            MenuItem("销售查询", Screen.SaleQuery),
            MenuItem("缺书登记", Screen.Reservation)
        )),
        MenuGroup("会员管理", listOf(
            MenuItem("会员列表", Screen.MemberManage),
            MenuItem("新建会员", Screen.NewMember),
            MenuItem("优惠政策", Screen.MemberPolicy),
            MenuItem("会员消费查询", Screen.MemberPurchaseLookup)
        )),
        MenuGroup("借阅管理", listOf(MenuItem("借阅管理", Screen.BorrowManage))),
        MenuGroup("客户反馈", listOf(MenuItem("反馈管理", Screen.FeedbackManage))),
        MenuGroup("图书检索", listOf(MenuItem("图书检索", Screen.BookSearch))),
    )

    "会员" -> listOf(
        MenuGroup("系统", listOf(MenuItem("修改密码", Screen.ChangePassword))),
        MenuGroup("个人信息", listOf(
            MenuItem("我的信息", Screen.MemberSelf),
            MenuItem("借阅记录", Screen.MemberBorrowHistory),
            MenuItem("消费记录", Screen.MemberPurchaseHistory)
        )),
        MenuGroup("会员政策", listOf(MenuItem("优惠政策", Screen.MemberPolicyView))),
        MenuGroup("图书检索", listOf(
            MenuItem("图书检索", Screen.BookSearch),
            MenuItem("缺书登记", Screen.Reservation)
        )),
        MenuGroup("客户反馈", listOf(MenuItem("提交反馈", Screen.FeedbackSubmit))),
    )

    "游客" -> listOf(
        MenuGroup("会员服务", listOf(MenuItem("注册会员", Screen.NewMember))),
        MenuGroup("图书检索", listOf(
            MenuItem("图书检索", Screen.BookSearch),
            MenuItem("缺书登记", Screen.Reservation)
        )),
        MenuGroup("客户反馈", listOf(MenuItem("提交反馈", Screen.FeedbackSubmit))),
    )

    else -> emptyList()
}
