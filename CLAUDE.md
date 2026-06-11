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
| 统计分析 | StatisticsScreen (含趋势图表 Tab + 分类分析环形图) |
| 借阅管理 | BorrowManageScreen |
| 图书检索 | BookSearchScreen |

## 新增功能（第二批）

| 功能 | 说明 |
|------|------|
| 工作台 Dashboard | 4 张统计卡片 + 库存预警列表 + 会员消费排行 + 最近操作日志 |
| 数据备份恢复 | ZIP 压缩备份、恢复、删除，自动清理旧备份 |
| 操作日志 | 查看操作日志，按类型筛选 |
| 系统配置 | 库存预警阈值、严重短缺阈值、备份保留数、每日滞纳金设置 |
| 统计分析增强 | 分类分析环形图 Tab |
| 数据导出 CSV | BookManageScreen, MemberManageScreen, SaleQueryScreen, PurchaseQueryScreen, ReturnQueryScreen, StockStatScreen, CategoryManageScreen, BorrowManageScreen |
| 滞纳金动态配置 | 借用系统配置中的每日滞纳金费率 |
| 网格浏览模式 | BookManageScreen 支持网格/列表双模式切换 |
| 批量操作 | BookManageScreen 批量删除、批量改价（批次选择框） |
| 角色菜单完善 | 经理/仓库管理员/售书员/会员/游客 各自菜单按需分配 |
