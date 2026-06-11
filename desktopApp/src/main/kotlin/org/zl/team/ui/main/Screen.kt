package org.zl.team.ui.main

sealed class Screen {

    // 系统
    data object ChangePassword : Screen()
    data object Logout : Screen()

    // 员工管理
    data object EmployeeManage : Screen()

    // 统计分析
    data object Statistics : Screen()

    // 图书管理
    data object BookManage : Screen()
    data object CategoryManage : Screen()

    // 书店简介
    data object BookstoreInfo : Screen()

    // 进货管理
    data object PurchaseRegister : Screen()
    data object PurchaseQuery : Screen()

    // 退货管理
    data object ReturnRegister : Screen()
    data object ReturnQuery : Screen()
    data object ReturnStat : Screen()

    // 库存管理
    data object StockStat : Screen()
    data object Reservation : Screen()

    // 销售管理
    data object Sell : Screen()
    data object TodayStat : Screen()
    data object SaleQuery : Screen()

    // 会员管理
    data object MemberManage : Screen()
    data object NewMember : Screen()
    data object MemberPolicy : Screen()
    data object MemberSelf : Screen()

    // 图书检索
    data object BookSearch : Screen()

    // 会员自助
    data object MemberBorrowHistory : Screen()
    data object MemberPurchaseHistory : Screen()
    data object MemberPolicyView : Screen()

    // 会员消费查询（售书员用）
    data object MemberPurchaseLookup : Screen()

    // 客户反馈
    data object FeedbackSubmit : Screen()
    data object FeedbackManage : Screen()

    // 供应商管理
    data object SupplierManage : Screen()

    // 财务管理
    data object Settlement : Screen()
    data object GrossProfit : Screen()

    // 借阅管理
    data object BorrowManage : Screen()
}
