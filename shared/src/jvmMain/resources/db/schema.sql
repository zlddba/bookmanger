-- 图书管理系统 建表脚本
-- 共 13 张表

-- 1. 系统用户表
CREATE TABLE IF NOT EXISTS admin (
    user_id  TEXT PRIMARY KEY,     -- 用户ID
    password TEXT NOT NULL,        -- 用户密码
    role     TEXT NOT NULL         -- 用户身份：经理/仓库管理员/售书员/会员/游客
);

-- 2. 书目信息表
CREATE TABLE IF NOT EXISTS book (
    book_id      TEXT PRIMARY KEY,                           -- 图书编号
    category_id  TEXT,                                       -- 图书分类号
    title        TEXT NOT NULL,                              -- 书名
    series       TEXT,                                       -- 丛书
    author       TEXT,                                       -- 作者
    publisher    TEXT,                                       -- 出版社
    edition      TEXT,                                       -- 版次
    isbn         TEXT,                                       -- ISBN
    price        REAL,                                       -- 定价
    stock        INTEGER DEFAULT 0,                          -- 库存量
    description  TEXT,                                       -- 内容简介
    keywords     TEXT,                                       -- 关键词
    publish_date TEXT,                                       -- 出版日期
    created_at   TEXT DEFAULT (datetime('now', 'localtime')) -- 入库时间
);

-- 3. 供应商表
CREATE TABLE IF NOT EXISTS supplier (
    supplier_id   TEXT PRIMARY KEY, -- 供应商编号
    name          TEXT NOT NULL,    -- 供应商名称
    address       TEXT,             -- 地址
    website       TEXT,             -- 网址
    contact       TEXT,             -- 联系人
    phone         TEXT,             -- 电话
    fax           TEXT,             -- 传真
    email         TEXT,             -- 电子邮件
    description   TEXT              -- 单位简介
);

-- 4. 会员表
CREATE TABLE IF NOT EXISTS member (
    card_no     TEXT PRIMARY KEY,                           -- 会员卡号
    level       INTEGER DEFAULT 1,                           -- 会员等级（1-5）
    name        TEXT NOT NULL,                              -- 姓名
    gender      TEXT,                                       -- 性别
    address     TEXT,                                       -- 地址
    company     TEXT,                                       -- 单位
    phone       TEXT,                                       -- 电话
    email       TEXT,                                       -- 电子邮件
    motto       TEXT,                                       -- 人生格言
    reg_date    TEXT DEFAULT (date('now', 'localtime')),    -- 注册日期
    total_spent REAL DEFAULT 0                              -- 累计消费额
);

-- 5. 会员政策表
CREATE TABLE IF NOT EXISTS member_policy (
    level       INTEGER PRIMARY KEY,  -- 会员级别（1-5）
    min_amount  INTEGER,           -- 会员标准：达到该金额可升级
    discount    TEXT DEFAULT '1.0', -- 打折（如 0.9）
    gift        TEXT,              -- 赠送礼品
    remark      TEXT               -- 备注
);

-- 6. 进书记录表
CREATE TABLE IF NOT EXISTS purchase_record (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,               -- ID
    supplier_id TEXT,                                            -- 供应商编号
    book_id     TEXT,                                            -- 图书编号
    quantity    INTEGER,                                         -- 数量
    unit_price  REAL,                                            -- 单价（进价）
    discount    REAL DEFAULT 1.0,                                -- 折扣
    amount      REAL,                                            -- 金额 = 数量*单价*折扣
    date        TEXT DEFAULT (datetime('now', 'localtime')),     -- 进书日期
    remark      TEXT                                             -- 备注
);

-- 7. 客户反馈表
CREATE TABLE IF NOT EXISTS feedback (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,               -- ID
    name        TEXT,                                            -- 姓名
    role        TEXT,                                            -- 身份
    gender      TEXT,                                            -- 性别
    company     TEXT,                                            -- 单位
    address     TEXT,                                            -- 地址
    email       TEXT,                                            -- 电子邮件
    content     TEXT,                                            -- 反馈信息
    date        TEXT DEFAULT (datetime('now', 'localtime'))      -- 反馈日期
);

-- 8. 售书记录表
CREATE TABLE IF NOT EXISTS sale_record (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,               -- ID
    book_id       TEXT,                                            -- 图书编号
    quantity      INTEGER,                                         -- 数量
    card_no       TEXT,                                            -- 会员卡号（可为空）
    discount      REAL DEFAULT 1.0,                                -- 实际打折
    amount        REAL,                                            -- 实收金额
    date          TEXT DEFAULT (date('now', 'localtime')),         -- 日期
    remark        TEXT                                             -- 备注
);

-- 9. 书店简介表
CREATE TABLE IF NOT EXISTS bookstore_info (
    name        TEXT PRIMARY KEY,  -- 书店名称
    address     TEXT,              -- 地址
    website     TEXT,              -- 网址
    contact     TEXT,              -- 联系人
    phone       TEXT,              -- 电话
    mobile      TEXT,              -- 手机
    email       TEXT,              -- 电子邮件
    description TEXT,              -- 书店简介
    remark      TEXT               -- 备注
);

-- 10. 图书分类表
CREATE TABLE IF NOT EXISTS book_category (
    category_id TEXT PRIMARY KEY,  -- 图书分类号
    name        TEXT NOT NULL,     -- 图书分类
    parent_id   TEXT               -- 所属父类编号（支持两级，顶级为空）
);

-- 11. 图书进价表
CREATE TABLE IF NOT EXISTS book_price (
    book_id  TEXT PRIMARY KEY,                              -- 图书编号
    price    REAL,                                          -- 进价（最近进价）
    date     TEXT DEFAULT (datetime('now', 'localtime'))    -- 进书日期
);

-- 12. 退货记录表
CREATE TABLE IF NOT EXISTS return_record (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,               -- ID
    supplier_id TEXT,                                            -- 供应商编号
    book_id     TEXT,                                            -- 图书编号
    unit_price  REAL,                                            -- 进价
    quantity    INTEGER,                                         -- 退货数量
    amount      REAL,                                            -- 金额
    reason      TEXT,                                            -- 退货原因
    date        TEXT DEFAULT (datetime('now', 'localtime')),     -- 退货日期
    remark      TEXT                                             -- 备注
);

-- 13. 员工表
CREATE TABLE IF NOT EXISTS employee (
    account     TEXT PRIMARY KEY,                                -- 员工帐号
    name        TEXT NOT NULL,                                   -- 姓名
    gender      TEXT,                                            -- 性别
    address     TEXT,                                            -- 地址
    phone       TEXT,                                            -- 电话
    mobile      TEXT,                                            -- 手机
    email       TEXT,                                            -- 电子邮件
    motto       TEXT,                                            -- 人生格言
    created_at  TEXT DEFAULT (datetime('now', 'localtime'))      -- 创建日期
);

-- 14. 图书预订/缺书登记表
CREATE TABLE IF NOT EXISTS book_reservation (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    book_title     TEXT NOT NULL,                                     -- 书名
    author         TEXT,                                              -- 作者
    publisher      TEXT,                                              -- 出版社
    isbn           TEXT,                                              -- ISBN
    customer_name  TEXT NOT NULL,                                     -- 客户姓名
    customer_phone TEXT,                                              -- 客户电话
    status         TEXT DEFAULT '待处理',                              -- 状态：待处理/已到货/已取消
    note           TEXT,                                              -- 备注
    created_at     TEXT DEFAULT (datetime('now', 'localtime')),       -- 登记日期
    resolved_at    TEXT                                               -- 处理日期
);

-- 15. 借阅记录表
CREATE TABLE IF NOT EXISTS borrow_record (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    book_id     TEXT NOT NULL,                                        -- 图书编号
    card_no     TEXT NOT NULL,                                        -- 会员卡号
    borrow_date TEXT NOT NULL,                                        -- 借阅日期
    due_date    TEXT NOT NULL,                                        -- 应还日期
    return_date TEXT,                                                 -- 实际归还日期
    status      TEXT DEFAULT '借阅中',                                 -- 状态：借阅中/已归还/逾期
    renew_count INTEGER DEFAULT 0,                                    -- 续借次数
    remark      TEXT                                                  -- 备注
);

-- ========================================
-- 种子数据：系统运行必需的基础数据
-- ========================================

-- 系统用户
INSERT OR IGNORE INTO admin (user_id, password, role) VALUES ('admin',    'admin', '经理');
INSERT OR IGNORE INTO admin (user_id, password, role) VALUES ('仓库管理员', 'admin', '仓库管理员');
INSERT OR IGNORE INTO admin (user_id, password, role) VALUES ('售书员',   'admin', '售书员');

-- 图书分类（两级：大类 → 小类，共17个）
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-01',     '计算机',     NULL);
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-01-01',  '程序设计',   'CAT-01');
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-01-02',  '数据库',     'CAT-01');
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-01-03',  '网络技术',   'CAT-01');

INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-02',     '文学',       NULL);
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-02-01',  '小说',       'CAT-02');
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-02-02',  '诗歌',       'CAT-02');

INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-03',     '经济管理',   NULL);
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-03-01',  '经济学',     'CAT-03');
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-03-02',  '管理学',     'CAT-03');

INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-04',     '教育',       NULL);
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-04-01',  '教材',       'CAT-04');
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-04-02',  '考试辅导',   'CAT-04');

INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-05',     '生活',       NULL);
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-05-01',  '健康养生',   'CAT-05');

INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-06',     '自然科学',   NULL);
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES ('CAT-06-01',  '数学',       'CAT-06');

-- 会员政策
INSERT OR IGNORE INTO member_policy (level, min_amount, discount, gift, remark) VALUES (1, 0,    '1.0',  NULL,             'Bronze Member — No Discount');
INSERT OR IGNORE INTO member_policy (level, min_amount, discount, gift, remark) VALUES (2, 500,  '0.95', 'Bookmark Set',   'Silver Member — 5% Off');
INSERT OR IGNORE INTO member_policy (level, min_amount, discount, gift, remark) VALUES (3, 1000, '0.90', 'Best Seller x1', 'Gold Member — 10% Off');
INSERT OR IGNORE INTO member_policy (level, min_amount, discount, gift, remark) VALUES (4, 2000, '0.85', '50 Yuan Coupon',     'Platinum Member — 15% Off');
INSERT OR IGNORE INTO member_policy (level, min_amount, discount, gift, remark) VALUES (5, 5000, '0.80', '100 Yuan Coupon',    'Diamond Member — 20% Off');

-- 书店简介
INSERT OR IGNORE INTO bookstore_info (name, address, website, contact, phone, mobile, email, description, remark) VALUES ('内电子信息学院书店', '北京市海淀区学院路1号', 'www.neicbookstore.cn', '张经理', '010-62208888', '13900008888', 'info@neicbookstore.cn', '校园书店，服务师生', '始于2005年');
