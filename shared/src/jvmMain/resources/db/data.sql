-- ========================================
-- 图书管理系统 模拟数据（非必需，仅用于演示）
-- 交易日期范围: 2026-05-01 ~ 2026-06-10
-- 种子数据（admin/category/member_policy/bookstore_info）已在 schema.sql 中
-- ========================================

-- 补充分类（种子数据中未包含的子类，供模拟书目使用）
INSERT
OR IGNORE INTO book_category (category_id, NAME, parent_id) VALUES ('CAT-01-04',  '操作系统',   'CAT-01');
INSERT
OR IGNORE INTO book_category (category_id, NAME, parent_id) VALUES ('CAT-01-05',  '人工智能',   'CAT-01');
INSERT
OR IGNORE INTO book_category (category_id, NAME, parent_id) VALUES ('CAT-03-03',  '市场营销',   'CAT-03');
INSERT
OR IGNORE INTO book_category (category_id, NAME, parent_id) VALUES ('CAT-04-03',  '外语学习',   'CAT-04');
INSERT
OR IGNORE INTO book_category (category_id, NAME, parent_id) VALUES ('CAT-05-02',  '旅游',       'CAT-05');
INSERT
OR IGNORE INTO book_category (category_id, NAME, parent_id) VALUES ('CAT-06-02',  '物理',       'CAT-06');

-- =====================
-- 员工资料 (employee) — 5人
-- =====================
INSERT
OR IGNORE INTO employee (account, NAME, gender, address, phone, mobile, email, motto, created_at) VALUES ('zhangwei',  '张伟',   '男', '北京市海淀区', '010-62200001', '13800000001', 'zhangwei@bookstore.cn',  '精益求精，服务读者',       '2026-05-01');
INSERT
OR IGNORE INTO employee (account, NAME, gender, address, phone, mobile, email, motto, created_at) VALUES ('liming',    '李明',   '男', '北京市朝阳区', '010-62200002', '13800000002', 'liming@bookstore.cn',    '诚信为本，质量第一',       '2026-05-01');
INSERT
OR IGNORE INTO employee (account, NAME, gender, address, phone, mobile, email, motto, created_at) VALUES ('wangfang',  '王芳',   '女', '北京市西城区', '010-62200003', '13800000003', 'wangfang@bookstore.cn',  '用心服务每一位读者',       '2026-05-01');
INSERT
OR IGNORE INTO employee (account, NAME, gender, address, phone, mobile, email, motto, created_at) VALUES ('zhaogang',  '赵刚',   '男', '北京市丰台区', '010-62200004', '13800000004', 'zhaogang@bookstore.cn',  '细节决定成败',             '2026-05-01');
INSERT
OR IGNORE INTO employee (account, NAME, gender, address, phone, mobile, email, motto, created_at) VALUES ('sunli',     '孙丽',   '女', '北京市通州区', '010-62200005', '13800000005', 'sunli@bookstore.cn',     '微笑服务，快乐工作',       '2026-05-01');

-- =====================
-- 供应商 (supplier) — 5家
-- =====================
INSERT
OR IGNORE INTO supplier (supplier_id, NAME, address, website, contact, phone, fax, email, description) VALUES ('SUP-001', '清华大学出版社',   '北京市海淀区清华园',    'www.tup.tsinghua.edu.cn',   '王主编', '010-62770001', '010-62770002', 'tup@tup.edu.cn',      '综合性大学出版社，以计算机图书见长');
INSERT
OR IGNORE INTO supplier (supplier_id, NAME, address, website, contact, phone, fax, email, description) VALUES ('SUP-002', '机械工业出版社',   '北京市西城区百万庄大街', 'www.cmpbook.com',           '李经理', '010-88370001', '010-88370002', 'cmp@cmpbook.com',     '工业技术类图书出版');
INSERT
OR IGNORE INTO supplier (supplier_id, NAME, address, website, contact, phone, fax, email, description) VALUES ('SUP-003', '人民邮电出版社',   '北京市丰台区成寿寺路',   'www.ptpress.com.cn',        '陈编辑', '010-81050001', '010-81050002', 'pt@ptpress.com.cn',   '信息技术与通信类图书');
INSERT
OR IGNORE INTO supplier (supplier_id, NAME, address, website, contact, phone, fax, email, description) VALUES ('SUP-004', '电子工业出版社',   '北京市海淀区万寿路',     'www.phei.com.cn',           '刘经理', '010-88250001', '010-88250002', 'phei@phei.com.cn',    '电子信息类图书出版');
INSERT
OR IGNORE INTO supplier (supplier_id, NAME, address, website, contact, phone, fax, email, description) VALUES ('SUP-005', '高等教育出版社',   '北京市西城区德外大街',   'www.hep.com.cn',            '赵主任', '010-58580001', '010-58580002', 'hep@hep.com.cn',      '高等教育教材出版');

-- =====================
-- 书目信息 (book) — 20本（已根据实际进销存更新库存）
-- =====================
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-001', 'CAT-01-01', 'Kotlin 程序设计',       '计算机系列丛书', '刘强',     '清华大学出版社',   '第2版',  '978-7-302-50001-1', 59.00,  52, '全面介绍 Kotlin 语言特性及在 Android 开发中的应用',  'Kotlin,Android,编程',           '2025-01-15', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-002', 'CAT-01-01', 'Java 核心技术',         '计算机科学丛书', '李明华',   '机械工业出版社',   '第11版', '978-7-111-50002-2', 79.00,  24, 'Java 经典入门教材，涵盖 Java 21 新特性',              'Java,核心技术,编程入门',        '2025-03-01', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-003', 'CAT-01-01', 'Python 编程从入门到实践', NULL,            '陈晨',     '人民邮电出版社',   '第3版',  '978-7-115-50003-3', 89.00,  47, '零基础学 Python，包含大量实战项目和练习',             'Python,编程入门,数据分析',      '2025-02-20', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-004', 'CAT-01-02', 'MySQL 必知必会',        '图灵程序设计丛书','周涛',     '人民邮电出版社',   '第5版',  '978-7-115-50004-4', 39.00,  23, 'SQL 快速入门，短小精悍，快速上手',                    'MySQL,SQL,数据库',              '2025-01-10', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-005', 'CAT-01-02', '数据库系统概念',        '计算机科学丛书', '吴健',     '机械工业出版社',   '第7版',  '978-7-111-50005-5', 99.00,  18, '数据库领域经典教材，涵盖关系模型、SQL、事务等',      '数据库,SQL,关系模型',           '2025-04-05', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-006', 'CAT-01-03', '计算机网络：自顶向下方法','计算机科学丛书', '黄丽',     '机械工业出版社',   '第8版',  '978-7-111-50006-6', 89.00,  18, '经典计算机网络教材，自顶向下讲解网络协议',            '计算机网络,TCP/IP,协议',        '2025-05-10', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-007', 'CAT-01-04', '深入理解计算机系统',   '计算机科学丛书', '郑宏',     '机械工业出版社',   '第4版',  '978-7-111-50007-7', 139.00, 16, '从程序员视角深入理解计算机系统底层原理',              '计算机系统,底层,汇编',          '2025-03-15', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-008', 'CAT-01-05', '深度学习',              '人工智能系列',   '孙浩然',   '清华大学出版社',   '第2版',  '978-7-302-50008-8', 149.00, 9,  '全面讲解深度学习核心算法：CNN、RNN、Transformer 等',  '深度学习,神经网络,AI',          '2025-04-20', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-009', 'CAT-02-01', '百年孤独',              NULL,              '加西亚·马尔克斯','清华大学出版社','第1版',  '978-7-302-50009-9', 55.00,  27, '魔幻现实主义经典之作，讲述布恩迪亚家族七代人的故事',  '小说,魔幻现实主义,经典',        '2024-06-01', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-010', 'CAT-02-01', '活着',                  NULL,              '余华',     '人民邮电出版社',   '第1版',  '978-7-115-50010-0', 45.00,  31, '讲述一个人历经世间沧桑和磨难的一生',                  '小说,余华,经典文学',            '2024-08-15', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-011', 'CAT-03-01', '经济学原理',            '经济学经典教材', '钱明',     '高等教育出版社',   '第8版',  '978-7-040-50011-1', 88.00,  16, '曼昆经典经济学入门教材中译本',                        '经济学,微观,宏观',              '2025-01-20', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-012', 'CAT-03-02', '管理学',                NULL,              '周志文',   '高等教育出版社',   '第14版', '978-7-040-50012-2', 79.00,  9, '罗宾斯管理学经典教材，管理理论与实践相结合',          '管理学,组织行为,领导力',        '2025-02-10', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-013', 'CAT-03-03', '营销管理',              NULL,              '菲利普·科特勒', '清华大学出版社',  '第16版', '978-7-302-50013-3', 99.00,  13, '营销学圣经，科特勒经典著作',                          '市场营销,品牌,消费者行为',      '2025-01-15', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-014', 'CAT-04-01', '高等数学（上册）',      '大学数学系列',   '同济大学数学系','高等教育出版社','第8版',  '978-7-040-50014-4', 49.00,  59, '理工科大学数学基础教材',                              '数学,高等数学,微积分',          '2025-01-01', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-015', 'CAT-04-02', '考研英语真题解析',      NULL,              '张剑',     '高等教育出版社',   '第10版', '978-7-040-50015-5', 69.00,  33, '近十年考研英语真题详细解析，含解题技巧',              '考研,英语,真题',                '2025-02-01', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-016', 'CAT-04-03', '新概念英语 2',          '新概念英语系列', '亚历山大', '高等教育出版社',   '第1版',  '978-7-040-50016-6', 35.00,  75, '经典英语学习教材，适合初中级学习者',                  '英语,新概念,语法',              '2024-09-01', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-017', 'CAT-05-01', '中医养生大全',          NULL,              '王中医',   '人民邮电出版社',   '第1版',  '978-7-115-50017-7', 68.00,  14, '系统介绍中医养生理论与实用方法',                      '中医,养生,健康',                '2025-01-05', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-018', 'CAT-05-02', '中国旅游完全指南',      NULL,              '马峰',     '电子工业出版社',   '第3版',  '978-7-121-50018-8', 88.00,  19, '覆盖全国各省份热门景点、美食、住宿攻略',              '旅游,攻略,中国',                '2025-03-10', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-019', 'CAT-06-01', '线性代数',              '大学数学系列',   '同济大学数学系','高等教育出版社','第7版',  '978-7-040-50019-9', 39.00,  27, '线性代数经典教材',                                    '数学,线性代数,矩阵',            '2025-01-01', '2026-05-01');
INSERT
OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES ('B-020', 'CAT-06-02', '大学物理学',            '大学物理系列',   '张三慧',   '清华大学出版社',   '第5版',  '978-7-302-50020-0', 65.00,  22, '理工科大学物理基础教材',                              '物理学,力学,电磁学',            '2025-01-01', '2026-05-01');

-- =====================
-- 会员 (member) — 7人
-- =====================
INSERT
OR IGNORE INTO member (card_no, LEVEL, NAME, gender, address, company, phone, email, motto, reg_date, total_spent) VALUES ('johnsmith',  1, 'John Smith',  'Male',   'Room 301, Building 5, Haidian District, Beijing',   'Tsinghua University',  '13900001001', 'johnsmith@email.com',    'Live and learn',                    '2026-05-10', 0);
INSERT
OR IGNORE INTO member (card_no, LEVEL, NAME, gender, address, company, phone, email, motto, reg_date, total_spent) VALUES ('lisawang',   2, 'Lisa Wang',   'Female', 'Room 502, Building 3, Chaoyang District, Beijing',   'Alibaba Group',        '13900001002', 'lisawang@email.com',     'Reading is my passion',              '2026-05-10', 708.70);
INSERT
OR IGNORE INTO member (card_no, LEVEL, NAME, gender, address, company, phone, email, motto, reg_date, total_spent) VALUES ('tomchen',    1, 'Tom Chen',    'Male',   'Room 101, Building 8, Xicheng District, Beijing',    'ByteDance Inc.',       '13900001003', 'tomchen@email.com',      'Knowledge is power',                 '2026-05-12', 124.00);
INSERT
OR IGNORE INTO member (card_no, LEVEL, NAME, gender, address, company, phone, email, motto, reg_date, total_spent) VALUES ('amyzhang',   3, 'Amy Zhang',   'Female', 'Room 702, Building 12, Fengtai District, Beijing',   'Microsoft China',      '13900001004', 'amyzhang@email.com',     'Books are the best friends',         '2026-05-08', 1112.40);
INSERT
OR IGNORE INTO member (card_no, LEVEL, NAME, gender, address, company, phone, email, motto, reg_date, total_spent) VALUES ('bobli',      1, 'Bob Li',      'Male',   'Room 203, Building 1, Tongzhou District, Beijing',   'JD.com',               '13900001005', 'bobli@email.com',        'Never stop learning',                '2026-05-15', 0);
INSERT
OR IGNORE INTO member (card_no, LEVEL, NAME, gender, address, company, phone, email, motto, reg_date, total_spent) VALUES ('gracewu',    1, 'Grace Wu',    'Female', 'Room 1608, Building A, Dongcheng District, Beijing', 'Baidu Inc.',           '13900001006', 'gracewu@email.com',      'Reading opens new worlds',           '2026-05-12', 207.00);
INSERT
OR IGNORE INTO member (card_no, LEVEL, NAME, gender, address, company, phone, email, motto, reg_date, total_spent) VALUES ('davidhe',    2, 'David He',    'Male',   'Room 901, Building B, Shijingshan District, Beijing','Huawei Technologies',  '13900001007', 'davidhe@email.com',      'Curiosity drives innovation',        '2026-05-10', 688.75);

-- =====================
-- 图书进价表 (book_price) — 每条记录为该书的最新进价
-- =====================
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-001', 35.00, '2026-05-25 09:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-002', 48.00, '2026-05-02 09:30:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-003', 55.00, '2026-05-28 14:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-004', 22.00, '2026-05-02 10:30:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-005', 60.00, '2026-05-02 11:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-006', 55.00, '2026-05-03 11:30:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-007', 85.00, '2026-06-05 11:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-008', 90.00, '2026-05-02 14:30:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-009', 30.00, '2026-05-02 15:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-010', 25.00, '2026-05-02 15:30:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-011', 52.00, '2026-05-03 09:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-012', 45.00, '2026-05-03 09:30:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-013', 60.00, '2026-05-03 10:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-014', 28.00, '2026-06-03 09:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-015', 40.00, '2026-05-03 11:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-016', 18.00, '2026-06-01 10:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-017', 38.00, '2026-05-03 14:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-018', 50.00, '2026-05-03 14:30:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-019', 22.00, '2026-05-03 15:00:00');
INSERT
OR IGNORE INTO book_price (book_id, price, DATE) VALUES ('B-020', 38.00, '2026-05-03 15:30:00');

-- =====================
-- 进书记录 (purchase_record) — 25条, 2026-05-02 ~ 2026-06-05
-- =====================
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (1,  'SUP-001', 'B-001', 30, 35.00, 1.0, 1050.00,  '2026-05-02 09:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (2,  'SUP-002', 'B-002', 25, 48.00, 1.0, 1200.00,  '2026-05-02 09:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (3,  'SUP-003', 'B-003', 35, 55.00, 1.0, 1925.00,  '2026-05-02 10:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (4,  'SUP-003', 'B-004', 30, 22.00, 1.0, 660.00,   '2026-05-02 10:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (5,  'SUP-002', 'B-005', 20, 60.00, 1.0, 1200.00,  '2026-05-02 11:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (6,  'SUP-002', 'B-006', 20, 55.00, 1.0, 1100.00,  '2026-05-02 11:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (7,  'SUP-002', 'B-007', 15, 85.00, 1.0, 1275.00,  '2026-05-02 14:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (8,  'SUP-001', 'B-008', 12, 90.00, 1.0, 1080.00,  '2026-05-02 14:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (9,  'SUP-001', 'B-009', 30, 30.00, 1.0, 900.00,   '2026-05-02 15:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (10, 'SUP-003', 'B-010', 40, 25.00, 1.0, 1000.00,  '2026-05-02 15:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (11, 'SUP-005', 'B-011', 20, 52.00, 1.0, 1040.00,  '2026-05-03 09:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (12, 'SUP-005', 'B-012', 20, 45.00, 1.0, 900.00,   '2026-05-03 09:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (13, 'SUP-001', 'B-013', 15, 60.00, 1.0, 900.00,   '2026-05-03 10:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (14, 'SUP-005', 'B-014', 50, 28.00, 1.0, 1400.00,  '2026-05-03 10:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (15, 'SUP-005', 'B-015', 35, 40.00, 1.0, 1400.00,  '2026-05-03 11:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (16, 'SUP-005', 'B-016', 60, 18.00, 1.0, 1080.00,  '2026-05-03 11:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (17, 'SUP-003', 'B-017', 15, 38.00, 1.0, 570.00,   '2026-05-03 14:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (18, 'SUP-004', 'B-018', 20, 50.00, 1.0, 1000.00,  '2026-05-03 14:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (19, 'SUP-005', 'B-019', 30, 22.00, 1.0, 660.00,   '2026-05-03 15:00:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (20, 'SUP-001', 'B-020', 25, 38.00, 1.0, 950.00,   '2026-05-03 15:30:00', '首批进货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (21, 'SUP-001', 'B-001', 10, 35.00, 1.0, 350.00,   '2026-05-25 09:00:00', '热销补货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (22, 'SUP-003', 'B-003', 15, 55.00, 1.0, 825.00,   '2026-05-28 14:00:00', '热销补货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (23, 'SUP-005', 'B-016', 20, 18.00, 1.0, 360.00,   '2026-06-01 10:00:00', '开学季备货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (24, 'SUP-005', 'B-014', 15, 28.00, 1.0, 420.00,   '2026-06-03 09:00:00', '期末补货');
INSERT
OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, DATE, remark) VALUES (25, 'SUP-002', 'B-007', 5,  85.00, 1.0, 425.00,   '2026-06-05 11:00:00', '补充库存');

-- =====================
-- 售书记录 (sale_record) — 48条, 2026-05-03 ~ 2026-06-10
-- =====================
-- 非会员 (10条)
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (1,  'B-001', 2, NULL, 1.0, 118.00,  '2026-05-03', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (2,  'B-002', 1, NULL, 1.0, 79.00,   '2026-05-03', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (3,  'B-004', 3, NULL, 1.0, 117.00,  '2026-05-04', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (4,  'B-010', 1, NULL, 1.0, 45.00,   '2026-05-05', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (5,  'B-016', 2, NULL, 1.0, 70.00,   '2026-05-05', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (6,  'B-014', 1, NULL, 1.0, 49.00,   '2026-05-06', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (7,  'B-003', 1, NULL, 1.0, 89.00,   '2026-05-07', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (8,  'B-006', 1, NULL, 1.0, 89.00,   '2026-05-08', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (9,  'B-010', 2, NULL, 1.0, 90.00,   '2026-05-10', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (10, 'B-019', 1, NULL, 1.0, 39.00,   '2026-05-13', NULL);

-- lisawang 银卡 0.95 (7条)
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (11, 'B-002', 1, 'lisawang', 0.95, 75.05,   '2026-05-04', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (12, 'B-011', 1, 'lisawang', 0.95, 83.60,   '2026-05-06', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (13, 'B-012', 2, 'lisawang', 0.95, 150.10,  '2026-05-08', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (14, 'B-007', 1, 'lisawang', 0.95, 132.05,  '2026-05-10', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (15, 'B-001', 2, 'lisawang', 0.95, 112.10,  '2026-05-12', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (16, 'B-005', 1, 'lisawang', 0.95, 94.05,   '2026-06-01', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (17, 'B-020', 1, 'lisawang', 0.95, 61.75,   '2026-06-03', '银卡会员 95折');

-- amyzhang 金卡 0.90 (13条)
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (18, 'B-003', 1, 'amyzhang', 0.90, 80.10,   '2026-05-03', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (19, 'B-005', 1, 'amyzhang', 0.90, 89.10,   '2026-05-04', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (20, 'B-009', 1, 'amyzhang', 0.90, 49.50,   '2026-05-05', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (21, 'B-008', 1, 'amyzhang', 0.90, 134.10,  '2026-05-06', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (22, 'B-007', 1, 'amyzhang', 0.90, 125.10,  '2026-05-07', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (23, 'B-013', 1, 'amyzhang', 0.90, 89.10,   '2026-05-08', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (24, 'B-001', 1, 'amyzhang', 0.90, 53.10,   '2026-05-09', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (25, 'B-006', 1, 'amyzhang', 0.90, 80.10,   '2026-05-10', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (26, 'B-011', 1, 'amyzhang', 0.90, 79.20,   '2026-05-11', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (27, 'B-020', 1, 'amyzhang', 0.90, 58.50,   '2026-05-12', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (28, 'B-015', 1, 'amyzhang', 0.90, 62.10,   '2026-05-13', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (29, 'B-012', 1, 'amyzhang', 0.90, 71.10,   '2026-06-01', '金卡会员 9折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (30, 'B-014', 2, 'amyzhang', 0.90, 88.20,   '2026-06-02', '金卡会员 9折');

-- tomchen 普通会员 (2条)
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (31, 'B-001', 1, 'tomchen',  1.0,  59.00,   '2026-05-05', '普通会员');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (32, 'B-020', 1, 'tomchen',  1.0,  65.00,   '2026-05-09', '普通会员');

-- gracewu 普通会员 (3条)
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (33, 'B-004', 1, 'gracewu',  1.0,  39.00,   '2026-05-07', '普通会员');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (34, 'B-013', 1, 'gracewu',  1.0,  99.00,   '2026-05-11', '普通会员');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (35, 'B-015', 1, 'gracewu',  1.0,  69.00,   '2026-06-02', '普通会员');

-- davidhe 银卡 0.95 (8条)
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (36, 'B-008', 1, 'davidhe',  0.95, 141.55,  '2026-05-04', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (37, 'B-001', 1, 'davidhe',  0.95, 56.05,   '2026-05-07', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (38, 'B-004', 2, 'davidhe',  0.95, 74.10,   '2026-05-09', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (39, 'B-011', 1, 'davidhe',  0.95, 83.60,   '2026-05-11', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (40, 'B-012', 1, 'davidhe',  0.95, 75.05,   '2026-05-13', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (41, 'B-007', 1, 'davidhe',  0.95, 132.05,  '2026-06-02', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (42, 'B-009', 1, 'davidhe',  0.95, 52.25,   '2026-06-04', '银卡会员 95折');
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no,    discount, amount, DATE, remark) VALUES (43, 'B-019', 2, 'davidhe',  0.95, 74.10,   '2026-06-06', '银卡会员 95折');

-- 6月上旬非会员 (5条)
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (44, 'B-003', 2, NULL, 1.0, 178.00,  '2026-06-03', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (45, 'B-010', 1, NULL, 1.0, 45.00,   '2026-06-05', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (46, 'B-016', 3, NULL, 1.0, 105.00,  '2026-06-07', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (47, 'B-017', 1, NULL, 1.0, 68.00,   '2026-06-08', NULL);
INSERT
OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, DATE, remark) VALUES (48, 'B-018', 1, NULL, 1.0, 88.00,   '2026-06-10', NULL);

-- =====================
-- 退货记录 (return_record) — 5条, 2026-05-25 ~ 2026-06-08
-- =====================
INSERT
OR IGNORE INTO return_record (id, supplier_id, book_id, unit_price, quantity, amount, reason, DATE, remark) VALUES (1, 'SUP-001', 'B-001', 35.00, 2, 70.00,  '封面破损',     '2026-05-25', '退换处理');
INSERT
OR IGNORE INTO return_record (id, supplier_id, book_id, unit_price, quantity, amount, reason, DATE, remark) VALUES (2, 'SUP-003', 'B-004', 22.00, 1, 22.00,  '装订错误',     '2026-05-28', '退换处理');
INSERT
OR IGNORE INTO return_record (id, supplier_id, book_id, unit_price, quantity, amount, reason, DATE, remark) VALUES (3, 'SUP-005', 'B-014', 28.00, 3, 84.00,  '印刷质量问题',  '2026-06-01', '已退回');
INSERT
OR IGNORE INTO return_record (id, supplier_id, book_id, unit_price, quantity, amount, reason, DATE, remark) VALUES (4, 'SUP-001', 'B-009', 30.00, 1, 30.00,  '运输损坏',     '2026-06-05', '已退回');
INSERT
OR IGNORE INTO return_record (id, supplier_id, book_id, unit_price, quantity, amount, reason, DATE, remark) VALUES (5, 'SUP-002', 'B-006', 55.00, 1, 55.00,  '发错书目',     '2026-06-08', '已退回');

-- =====================
-- 图书预订 (book_reservation) — 5条, 2026-05-20 ~ 2026-06-05
-- =====================
INSERT
OR IGNORE INTO book_reservation (id, book_title, author, publisher, isbn, customer_name, customer_phone, status, note, created_at, resolved_at) VALUES (1, 'Effective Java',                       'Joshua Bloch',        'Addison-Wesley', '978-0-134-68599-1', 'John Smith',  '13900001001', '待处理', '图书馆没有这本书，希望尽快进货',                          '2026-05-20 10:00:00', NULL);
INSERT
OR IGNORE INTO book_reservation (id, book_title, author, publisher, isbn, customer_name, customer_phone, status, note, created_at, resolved_at) VALUES (2, 'Clean Architecture',                   'Robert C. Martin',    'Prentice Hall',  '978-0-134-49416-6', 'Lisa Wang',   '13900001002', '已到货', '已通知客户来取',                                            '2026-05-22 14:00:00', '2026-06-01 09:00:00');
INSERT
OR IGNORE INTO book_reservation (id, book_title, author, publisher, isbn, customer_name, customer_phone, status, note, created_at, resolved_at) VALUES (3, 'Introduction to Algorithms',           'Thomas H. Cormen',    'MIT Press',      '978-0-262-04630-5', 'Amy Zhang',   '13900001004', '待处理', '要求正版，不接受影印版',                                    '2026-05-25 11:00:00', NULL);
INSERT
OR IGNORE INTO book_reservation (id, book_title, author, publisher, isbn, customer_name, customer_phone, status, note, created_at, resolved_at) VALUES (4, 'The Pragmatic Programmer',             'David Thomas',        'Addison-Wesley', '978-0-135-95705-9', 'David He',    '13900001007', '已取消', '客户主动取消',                                              '2026-05-28 16:00:00', '2026-06-03 10:00:00');
INSERT
OR IGNORE INTO book_reservation (id, book_title, author, publisher, isbn, customer_name, customer_phone, status, note, created_at, resolved_at) VALUES (5, 'Designing Data-Intensive Applications', 'Martin Kleppmann',    'Reilly Media',  '978-1-449-37332-0', 'Bob Li',      '13900001005', '待处理', '需要英文原版',                                              '2026-06-05 08:00:00', NULL);

-- =====================
-- 客户反馈 (feedback) — 6条, 2026-05-25 ~ 2026-06-10
-- =====================
INSERT
OR IGNORE INTO feedback (id, NAME, ROLE, gender, company, address, email, content, DATE) VALUES (1, 'John Smith',  '会员', 'Male',   'Tsinghua University',  'Haidian, Beijing',     'johnsmith@email.com',    '书店环境很好，图书种类齐全，建议增加更多外文原版书',                               '2026-05-25 10:00:00');
INSERT
OR IGNORE INTO feedback (id, NAME, ROLE, gender, company, address, email, content, DATE) VALUES (2, 'Lisa Wang',   '会员', 'Female', 'Alibaba Group',        'Chaoyang, Beijing',    'lisawang@email.com',     '会员折扣很实惠，银卡95折不错，但希望增加积分兑换功能',                            '2026-05-28 14:00:00');
INSERT
OR IGNORE INTO feedback (id, NAME, ROLE, gender, company, address, email, content, DATE) VALUES (3, 'Visitor',     '游客', NULL,    NULL,                   NULL,                   NULL,                     '第一次来这个书店，整体感觉不错，以后会常来',                                       '2026-06-01 09:00:00');
INSERT
OR IGNORE INTO feedback (id, NAME, ROLE, gender, company, address, email, content, DATE) VALUES (4, 'Amy Zhang',   '会员', 'Female', 'Microsoft China',      'Fengtai, Beijing',     'amyzhang@email.com',     '金卡的9折力度希望可以再提高一点，另外建议定期举办读书会活动',                    '2026-06-03 16:00:00');
INSERT
OR IGNORE INTO feedback (id, NAME, ROLE, gender, company, address, email, content, DATE) VALUES (5, 'Tom Chen',    '会员', 'Male',   'ByteDance Inc.',       'Xicheng, Beijing',     'tomchen@email.com',      '购书流程很顺畅，建议增加微信小程序方便手机端浏览',                                '2026-06-07 11:00:00');
INSERT
OR IGNORE INTO feedback (id, NAME, ROLE, gender, company, address, email, content, DATE) VALUES (6, 'David He',    '会员', 'Male',   'Huawei Technologies',  'Shijingshan, Beijing', 'davidhe@email.com',      '周末营业时间希望能延长，工作日下班后来不及',                                       '2026-06-10 18:00:00');


-- =====================================================
-- 补充模拟数据（使所有表均有充足记录）
-- 日期范围：2026-05-01 ～ 2026-06-10
-- =====================================================

-- 1. 补充系统用户（admin）
INSERT OR IGNORE INTO admin (user_id, password, role) VALUES
('zhangwei', '123456', '仓库管理员'),
('liming',   '123456', '售书员'),
('wangfang', '123456', '售书员'),
('lijing',   '123456', '经理'),
('zhaoyun',  '123456', '仓库管理员');

-- 2. 补充员工（employee）
INSERT OR IGNORE INTO employee (account, name, gender, address, phone, mobile, email, motto, created_at) VALUES
('lijing',    '李静',   '女', '北京市东城区', '010-62200006', '13800000006', 'lijing@bookstore.cn',    '用心管理，服务至上',       '2026-05-01'),
('zhaoyun',   '赵云',   '男', '北京市大兴区', '010-62200007', '13800000007', 'zhaoyun@bookstore.cn',   '库存准确，效率第一',       '2026-05-01'),
('zhoujie',   '周杰',   '男', '北京市顺义区', '010-62200008', '13800000008', 'zhoujie@bookstore.cn',   '热情待客，专业推荐',       '2026-05-01'),
('wuying',    '吴英',   '女', '北京市昌平区', '010-62200009', '13800000009', 'wuying@bookstore.cn',    '书香伴我行',               '2026-05-01'),
('zhengnan',  '郑楠',   '女', '北京市石景山区', '010-62200010', '13800000010', 'zhengnan@bookstore.cn',  '阅读改变生活',             '2026-05-01');

-- 3. 补充供应商（supplier）
INSERT OR IGNORE INTO supplier (supplier_id, name, address, website, contact, phone, fax, email, description) VALUES
('SUP-006', '北京大学出版社',   '北京市海淀区成府路',   'www.pup.cn',            '张编辑', '010-62760001', '010-62760002', 'pup@pup.cn',          '人文社科类图书出版'),
('SUP-007', '中信出版社',       '北京市朝阳区惠新东街', 'www.citicpub.com',      '王经理', '010-84860001', '010-84860002', 'citic@citicpub.com',  '经管畅销书出版'),
('SUP-008', '科学出版社',       '北京市东城区东黄城根', 'www.sciencep.com',      '李主任', '010-64000001', '010-64000002', 'science@sciencep.com','科技学术专著'),
('SUP-009', '外语教学与研究出版社', '北京市海淀区西三环', 'www.fltrp.com',        '陈老师', '010-88810001', '010-88810002', 'fltrp@fltrp.com',     '外语类图书出版'),
('SUP-010', '人民文学出版社',   '北京市东城区朝内大街', 'www.rw-cn.com',         '刘总编', '010-65260001', '010-65260002', 'rw@rw-cn.com',        '文学类经典出版');

-- 4. 补充图书分类（若需要更细分类）
INSERT OR IGNORE INTO book_category (category_id, name, parent_id) VALUES
('CAT-01-06',  '前端开发',   'CAT-01'),
('CAT-02-03',  '散文',       'CAT-02'),
('CAT-03-04',  '金融',       'CAT-03'),
('CAT-04-04',  '职业教育',   'CAT-04'),
('CAT-05-03',  '烹饪美食',   'CAT-05');

-- 5. 补充图书（10本新书）
INSERT OR IGNORE INTO book (book_id, category_id, title, series, author, publisher, edition, isbn, price, stock, description, keywords, publish_date, created_at) VALUES
('B-021', 'CAT-01-06', 'Vue.js 从入门到项目实战', '前端系列', '黄鑫', '人民邮电出版社', '第1版', '978-7-115-50021-1', 79.00, 30, 'Vue3 全家桶开发指南', 'Vue,前端,JavaScript', '2025-06-01', '2026-05-01'),
('B-022', 'CAT-01-06', 'React 进阶之路',        '前端系列', '徐飞', '电子工业出版社', '第2版', '978-7-121-50022-2', 89.00, 25, 'React 核心原理与性能优化', 'React,前端,组件', '2025-07-01', '2026-05-01'),
('B-023', 'CAT-02-03', '瓦尔登湖',              NULL,      '梭罗', '人民文学出版社', '第1版', '978-7-02-50023-3', 45.00, 20, '简单生活的哲学', '散文,哲学,自然', '2024-12-01', '2026-05-01'),
('B-024', 'CAT-02-01', '平凡的世界',            NULL,      '路遥', '北京大学出版社', '第3版', '978-7-301-50024-4', 88.00, 18, '茅盾文学奖作品', '小说,奋斗,农村', '2024-10-01', '2026-05-01'),
('B-025', 'CAT-03-04', '价值',                  NULL,      '张磊', '中信出版社', '第1版', '978-7-5086-50025-5', 79.00, 15, '高瓴资本的投资哲学', '投资,价值,金融', '2025-03-01', '2026-05-01'),
('B-026', 'CAT-03-01', '国富论',                '经济学经典', '亚当·斯密', '科学出版社', '第1版', '978-7-03-50026-6', 128.00, 10, '现代经济学奠基之作', '经济学,市场,国富', '2024-05-01', '2026-05-01'),
('B-027', 'CAT-04-04', 'Python 数据分析（职业教育）', NULL, '杨文', '高等教育出版社', '第1版', '978-7-04-50027-7', 59.00, 40, '高职高专数据分析教材', '数据分析,Python,职教', '2025-08-01', '2026-05-01'),
('B-028', 'CAT-05-03', '家常菜谱大全',          NULL,      '美食家', '人民邮电出版社', '第1版', '978-7-115-50028-8', 49.00, 35, '1000道家常菜', '烹饪,菜谱,美食', '2025-01-01', '2026-05-01'),
('B-029', 'CAT-06-02', '时间简史',              NULL,      '霍金', '科学出版社', '第1版', '978-7-03-50029-9', 68.00, 12, '宇宙学经典科普', '物理,宇宙,黑洞', '2024-11-01', '2026-05-01'),
('B-030', 'CAT-01-05', '机器学习实战',          '人工智能', '赵宇', '清华大学出版社', '第2版', '978-7-302-50030-0', 99.00, 20, '基于 Scikit-learn 和 TensorFlow', '机器学习,Python,AI', '2025-05-01', '2026-05-01');

-- 6. 补充会员（8人）
INSERT OR IGNORE INTO member (card_no, level, name, gender, address, company, phone, email, motto, reg_date, total_spent) VALUES
('sarahli',    1, 'Sarah Li',   'Female', 'Room 1203, Building C, Chaoyang District, Beijing', 'Tencent',         '13900001008', 'sarahli@email.com',   'Read more, know more',        '2026-05-18', 0),
('mikechen',   2, 'Mike Chen',   'Male',   'Room 405, Building 2, Dongcheng District, Beijing', 'Didi Chuxing',    '13900001009', 'mikechen@email.com',   'Code and books',              '2026-05-20', 0),
('lindazhao',  3, 'Linda Zhao',  'Female', 'Room 888, Building 6, Haidian District, Beijing',   'Baidu AI',        '13900001010', 'lindazhao@email.com',  'AI for better world',         '2026-05-05', 0),
('kevinxu',    1, 'Kevin Xu',    'Male',   'Room 210, Building D, Fengtai District, Beijing',   'Xiaomi',          '13900001011', 'kevinxu@email.com',     'Never give up',               '2026-05-22', 0),
('oliviawang', 2, 'Olivia Wang', 'Female', 'Room 1506, Building A, Xicheng District, Beijing',  'ByteDance',       '13900001012', 'oliviawang@email.com',  'Keep reading, keep growing',  '2026-05-15', 0),
('peterli',    1, 'Peter Li',    'Male',   'Room 701, Building 9, Shijingshan District, Beijing','JD.com',          '13900001013', 'peterli@email.com',     'Books are my treasure',       '2026-05-28', 0),
('angelahuang',2, 'Angela Huang','Female', 'Room 505, Building 11, Tongzhou District, Beijing', 'Meituan',         '13900001014', 'angelahuang@email.com','Reading makes me happy',       '2026-05-12', 0),
('jasonzhou',  1, 'Jason Zhou',  'Male',   'Room 333, Building 7, Daxing District, Beijing',    'Didiglobal',      '13900001015', 'jasonzhou@email.com',   'Learning is endless',         '2026-06-01', 0);

-- 7. 补充进书记录（30条，含新书首批进货及部分旧书补货）
-- 新书首批进货
INSERT OR IGNORE INTO purchase_record (id, supplier_id, book_id, quantity, unit_price, discount, amount, date, remark) VALUES
(26, 'SUP-003', 'B-021', 30, 45.00, 1.0, 1350.00, '2026-05-03 08:30:00', '新书上架'),
(27, 'SUP-004', 'B-022', 25, 52.00, 1.0, 1300.00, '2026-05-03 09:00:00', '新书上架'),
(28, 'SUP-010', 'B-023', 20, 25.00, 1.0, 500.00,  '2026-05-04 10:00:00', '新书上架'),
(29, 'SUP-006', 'B-024', 18, 50.00, 1.0, 900.00,  '2026-05-04 11:00:00', '新书上架'),
(30, 'SUP-007', 'B-025', 15, 45.00, 1.0, 675.00,  '2026-05-05 09:30:00', '新书上架'),
(31, 'SUP-008', 'B-026', 10, 75.00, 1.0, 750.00,  '2026-05-05 14:00:00', '新书上架'),
(32, 'SUP-005', 'B-027', 40, 32.00, 1.0, 1280.00, '2026-05-06 10:00:00', '新书上架'),
(33, 'SUP-003', 'B-028', 35, 28.00, 1.0, 980.00,  '2026-05-06 15:00:00', '新书上架'),
(34, 'SUP-008', 'B-029', 12, 38.00, 1.0, 456.00,  '2026-05-07 09:00:00', '新书上架'),
(35, 'SUP-001', 'B-030', 20, 58.00, 1.0, 1160.00, '2026-05-07 11:00:00', '新书上架'),
-- 旧书补货（热销补充）
(36, 'SUP-001', 'B-001', 15, 35.00, 1.0, 525.00,  '2026-06-02 09:00:00', '热销补货'),
(37, 'SUP-003', 'B-003', 20, 55.00, 1.0, 1100.00, '2026-06-04 14:00:00', '热销补货'),
(38, 'SUP-002', 'B-007', 8,  85.00, 1.0, 680.00,  '2026-06-06 11:00:00', '补货'),
(39, 'SUP-005', 'B-016', 30, 18.00, 1.0, 540.00,  '2026-06-08 10:00:00', '开学季补货'),
(40, 'SUP-003', 'B-010', 20, 25.00, 1.0, 500.00,  '2026-05-28 16:00:00', '加印补货');

-- 8. 补充售书记录（50条，涵盖新旧图书，涉及会员与非会员，日期分散）
-- 新书销售（部分会员）
INSERT OR IGNORE INTO sale_record (id, book_id, quantity, card_no, discount, amount, date, remark) VALUES
(49, 'B-021', 2, 'sarahli',  1.0, 158.00, '2026-05-10', '普通会员'),
(50, 'B-022', 1, 'lindazhao',0.90, 80.10, '2026-05-12', '金卡会员 9折'),
(51, 'B-023', 1, NULL,       1.0, 45.00,  '2026-05-13', NULL),
(52, 'B-024', 1, 'mikechen', 0.95, 83.60, '2026-05-15', '银卡会员 95折'),
(53, 'B-025', 1, 'oliviawang',0.95, 75.05, '2026-05-18', '银卡会员 95折'),
(54, 'B-026', 1, NULL,       1.0, 128.00, '2026-05-20', NULL),
(55, 'B-027', 3, 'angelahuang',0.95,168.15, '2026-05-22', '银卡会员 95折'),
(56, 'B-028', 2, NULL,       1.0, 98.00,  '2026-05-24', NULL),
(57, 'B-029', 1, 'kevinxu',  1.0, 68.00,  '2026-05-26', '普通会员'),
(58, 'B-030', 1, 'peterli',  1.0, 99.00,  '2026-05-28', '普通会员'),
-- 老书继续销售
(59, 'B-001', 1, 'jasonzhou',1.0, 59.00,  '2026-06-01', '普通会员'),
(60, 'B-004', 2, 'sarahli',  1.0, 78.00,  '2026-06-02', '普通会员'),
(61, 'B-008', 1, 'lindazhao',0.90,134.10, '2026-06-03', '金卡会员 9折'),
(62, 'B-011', 1, 'mikechen', 0.95, 83.60, '2026-06-04', '银卡会员 95折'),
(63, 'B-014', 2, NULL,       1.0, 98.00,  '2026-06-05', NULL),
(64, 'B-016', 5, 'angelahuang',0.95,166.25, '2026-06-06', '银卡会员 95折'),
(65, 'B-019', 1, 'oliviawang',0.95, 37.05, '2026-06-07', '银卡会员 95折'),
(66, 'B-020', 2, NULL,       1.0, 130.00, '2026-06-08', NULL),
(67, 'B-003', 2, 'jasonzhou',1.0, 178.00, '2026-06-09', '普通会员'),
(68, 'B-007', 1, 'peterli',  1.0, 139.00, '2026-06-10', '普通会员'),
-- 更多非会员销售
(69, 'B-002', 1, NULL, 1.0, 79.00,  '2026-05-11', NULL),
(70, 'B-005', 1, NULL, 1.0, 99.00,  '2026-05-14', NULL),
(71, 'B-006', 2, NULL, 1.0, 178.00, '2026-05-17', NULL),
(72, 'B-009', 3, NULL, 1.0, 165.00, '2026-05-19', NULL),
(73, 'B-012', 1, NULL, 1.0, 79.00,  '2026-05-21', NULL),
(74, 'B-013', 1, NULL, 1.0, 99.00,  '2026-05-23', NULL),
(75, 'B-015', 2, NULL, 1.0, 138.00, '2026-05-25', NULL),
(76, 'B-017', 1, NULL, 1.0, 68.00,  '2026-05-27', NULL),
(77, 'B-018', 1, NULL, 1.0, 88.00,  '2026-05-29', NULL),
(78, 'B-021', 1, NULL, 1.0, 79.00,  '2026-06-04', NULL);

-- 9. 补充退货记录（5条）
INSERT OR IGNORE INTO return_record (id, supplier_id, book_id, unit_price, quantity, amount, reason, date, remark) VALUES
(6,  'SUP-003', 'B-021', 45.00, 1, 45.00,  '封面污损',     '2026-05-27', '换货处理'),
(7,  'SUP-007', 'B-025', 45.00, 2, 90.00,  '印刷缺页',     '2026-05-30', '已退回'),
(8,  'SUP-001', 'B-001', 35.00, 1, 35.00,  '客户退货',     '2026-06-02', '七天无理由'),
(9,  'SUP-005', 'B-027', 32.00, 3, 96.00,  '教材改版',     '2026-06-07', '退回出版社'),
(10, 'SUP-010', 'B-023', 25.00, 1, 25.00,  '运输破损',     '2026-06-09', '已处理');

-- 10. 补充图书预订（5条）
INSERT OR IGNORE INTO book_reservation (id, book_title, author, publisher, isbn, customer_name, customer_phone, status, note, created_at, resolved_at) VALUES
(6,  'AI 超级个体',            '李开复',   '中信出版社',   '978-7-5086-50036-3', 'Linda Zhao',   '13900001004', '待处理', '希望第一时间到货',          '2026-06-01 09:00:00', NULL),
(7,  '深入理解ES6',            'Nicholas', '人民邮电出版社', '978-7-115-50037-8', 'Mike Chen',    '13900001009', '已到货', '已通知取书',                '2026-05-26 14:00:00', '2026-06-05 10:00:00'),
(8,  '乌合之众',               '勒庞',     '人民文学出版社', '978-7-02-50038-5', 'Sarah Li',     '13900001008', '待处理', '需要法文直译版',            '2026-06-03 11:00:00', NULL),
(9,  'JavaScript 高级程序设计', 'Zakas',   '人民邮电出版社', '978-7-115-50039-9', 'Kevin Xu',     '13900001011', '已取消', '客户购买了其他版本',        '2026-05-29 16:00:00', '2026-06-06 09:00:00'),
(10, '芯片简史',               '汪波',     '科学出版社',     '978-7-03-50040-0', 'Angela Huang', '13900001014', '待处理', '需要签名版',                '2026-06-08 08:00:00', NULL);

-- 11. 补充客户反馈（5条）
INSERT OR IGNORE INTO feedback (id, name, role, gender, company, address, email, content, date) VALUES
(7,  'Sarah Li',     '会员', 'Female', 'Tencent',       'Chaoyang, Beijing', 'sarahli@email.com',    '新书上架速度快，赞',                                   '2026-06-02 14:00:00'),
(8,  'Mike Chen',    '会员', 'Male',   'Didi Chuxing',  'Dongcheng, Beijing','mikechen@email.com',   '希望增加英文原版计算机书',                             '2026-06-04 10:30:00'),
(9,  'Linda Zhao',   '会员', 'Female', 'Baidu AI',      'Haidian, Beijing',  'lindazhao@email.com',  '金卡会员福利很好，会继续支持',                         '2026-06-06 16:00:00'),
(10, 'Visitor2',     '游客', NULL,    NULL,             NULL,                NULL,                   '书店布置温馨，体验不错',                               '2026-06-08 11:00:00'),
(11, 'Kevin Xu',     '会员', 'Male',   'Xiaomi',         'Fengtai, Beijing',  'kevinxu@email.com',     '建议提供自习区',                                       '2026-06-10 09:00:00');

-- =====================================================
-- 更新库存（基于新增的进销存）
-- =====================================================
-- 注意：以下 UPDATE 语句需在原数据及补充数据全部执行后运行
-- 计算逻辑：stock = 原库存 + 进货总量 - 销售总量 - 退货总量

-- 新书库存初始化已在插入时设置，但补充销售和退货会减少，需要重新计算
UPDATE book SET stock = stock + (
    SELECT IFNULL(SUM(quantity), 0) FROM purchase_record WHERE book_id = book.book_id
) - (
                            SELECT IFNULL(SUM(quantity), 0) FROM sale_record WHERE book_id = book.book_id
                        ) - (
                            SELECT IFNULL(SUM(quantity), 0) FROM return_record WHERE book_id = book.book_id
                        );

-- 由于上述 UPDATE 会重复累计之前已经计算过的，更安全的方式是直接重新计算所有书的库存
-- 为避免重复，我们使用子查询一次性修正

-- 对于已有库存的书（包括新书），先重置为进货总量减销售减退货
UPDATE book SET stock = (
                            SELECT IFNULL(SUM(quantity), 0) FROM purchase_record WHERE purchase_record.book_id = book.book_id
                        ) - (
                            SELECT IFNULL(SUM(quantity), 0) FROM sale_record WHERE sale_record.book_id = book.book_id
                        ) - (
                            SELECT IFNULL(SUM(quantity), 0) FROM return_record WHERE return_record.book_id = book.book_id
                        );

-- =====================================================
-- 更新会员累计消费（基于新增的销售）
-- =====================================================
UPDATE member SET total_spent = (
    SELECT IFNULL(SUM(amount), 0) FROM sale_record WHERE sale_record.card_no = member.card_no
);

-- 补充新书的最新进价（book_price）
INSERT OR REPLACE INTO book_price (book_id, price, date) VALUES
('B-021', 45.00, '2026-05-03 08:30:00'),
('B-022', 52.00, '2026-05-03 09:00:00'),
('B-023', 25.00, '2026-05-04 10:00:00'),
('B-024', 50.00, '2026-05-04 11:00:00'),
('B-025', 45.00, '2026-05-05 09:30:00'),
('B-026', 75.00, '2026-05-05 14:00:00'),
('B-027', 32.00, '2026-05-06 10:00:00'),
('B-028', 28.00, '2026-05-06 15:00:00'),
('B-029', 38.00, '2026-05-07 09:00:00'),
('B-030', 58.00, '2026-05-07 11:00:00');

-- =====================
-- 借阅记录 (borrow_record) — 5条, 2026-05-20 ~ 2026-06-10
-- =====================
INSERT OR IGNORE INTO borrow_record (id, book_id, card_no, borrow_date, due_date, return_date, status, renew_count, remark) VALUES
(1, 'B-021', 'lisawang',  '2026-05-20', '2026-06-19', NULL,          '借阅中', 0, '初次借阅'),
(2, 'B-003', 'amyzhang',  '2026-05-25', '2026-06-24', NULL,          '借阅中', 0, NULL),
(3, 'B-001', 'tomchen',   '2026-05-28', '2026-06-27', '2026-06-05',  '已归还', 0, '提前归还'),
(4, 'B-007', 'davidhe',   '2026-06-01', '2026-07-01', NULL,          '借阅中', 1, '已续借一次'),
(5, 'B-014', 'gracewu',   '2026-06-10', '2026-07-10', NULL,          '借阅中', 0, NULL);