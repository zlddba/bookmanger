# CLAUDE.md

## 项目概述

从 `tsglxt/`（Kotlin + JavaFX + MyBatis + SQLite）迁移 UI 层到 Compose Desktop 的图书管理系统。后端代码（entity/mapper/service/config/util）从 tsglxt 完整复用，仅改包名。

## 构建与开发命令

```bash
# 环境配置
export JAVA_HOME=/e/development/java/java21
export GRADLE_HOME=/e/development/gradle/gradle-9.5.1
export GRADLE_USER_HOME=/e/development/gradle/gradlemirror
export PATH=$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH

# 热重载运行（自动 reload）
gradle :desktopApp:hotRun --auto --no-daemon --offline

# 编译检查
gradle :desktopApp:compileKotlin

# 运行测试
gradle :shared:jvmTest
```

## 架构

```
Composable Screen (Material 3)    ← 30 screens
    ↓
Service (object 单例)
    ↓
Mapper (MyBatis 接口)
    ↓
SQLite (单文件 bookstore.db)
```

- 后端（shared/jvmMain）复用 tsglxt 代码，零逻辑改动
- 前端（desktopApp）30 个 Composable 对应 30 个 FXML
- 角色菜单权限与 tsglxt 一致（5 个角色）

## 已完成功能（30 个界面）

| 模块 | 界面 |
|------|------|
| 登录 | LoginScreen (3次限制+游客), ChangePasswordScreen |
| 员工管理 | EmployeeManageScreen |
| 图书管理 | BookManageScreen, CategoryManageScreen, BookstoreInfoScreen |
| 进货管理 | PurchaseRegisterScreen, PurchaseQueryScreen, SupplierManageScreen |
| 退货管理 | ReturnRegisterScreen, ReturnQueryScreen, ReturnStatScreen |
| 库存管理 | StockStatScreen, ReservationScreen |
| 销售管理 | SellScreen (含会员折扣), TodayStatScreen, SaleQueryScreen |
| 会员管理 | MemberManageScreen, NewMemberScreen, MemberPolicyScreen, MemberSelfScreen |
| 客户反馈 | FeedbackSubmitScreen, FeedbackManageScreen |
| 财务管理 | SettlementScreen, GrossProfitScreen |
| 统计分析 | StatisticsScreen (含趋势图表 Tab) |
| 借阅管理 | BorrowManageScreen |
| 图书检索 | BookSearchScreen |
