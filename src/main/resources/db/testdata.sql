-- =====================================================
-- 在线购物系统测试数据填充脚本
-- 第二部分：插入测试假数据
-- =====================================================

USE online_shopping_db;

-- =====================================================
-- 1. 插入用户数据 (online_shopping_user)
-- =====================================================

-- 插入普通用户（user_type = 0）
INSERT INTO online_shopping_user (user_type, name, email, address, phone) VALUES
(0, '张三', 'zhangsan@example.com', '北京市朝阳区建国路88号', '13800138001'),
(0, '李四', 'lisi@example.com', '上海市浦东新区陆家嘴环路1000号', '13800138002'),
(0, '王五', 'wangwu@example.com', '广州市天河区珠江新城花城大道100号', '13800138003'),
(0, '赵六', 'zhaoliu@example.com', '深圳市南山区科技园南区科苑路15号', '13800138004'),
(0, '钱七', 'qianqi@example.com', '杭州市西湖区文一西路50号', '13800138005'),
(0, '孙八', 'sunba@example.com', '成都市武侯区天府大道中段666号', '13800138006'),
(0, '周九', 'zhoujiu@example.com', '武汉市洪山区珞喻路1037号', '13800138007'),
(0, '吴十', 'wushi@example.com', '南京市玄武区中山路1号', '13800138008'),
(0, '郑十一', 'zhengshiyi@example.com', '西安市雁塔区小寨东路126号', '13800138009'),
(0, '陈十二', 'chenshier@example.com', '重庆市渝北区龙溪街道红锦大道99号', '13800138010');

-- 插入商家用户（user_type = 1）
INSERT INTO online_shopping_user (user_type, name, email, address, phone) VALUES
(1, '小米旗舰店', 'xiaomi@shop.com', '北京市海淀区清河中街68号', '13900139001'),
(1, '华为官方店', 'huawei@shop.com', '深圳市龙岗区坂田华为基地', '13900139002'),
(1, '苹果授权店', 'apple@shop.com', '上海市静安区南京西路1266号', '13900139003'),
(1, '耐克运动专营店', 'nike@shop.com', '广州市越秀区环市东路371号', '13900139004'),
(1, '优衣库旗舰店', 'uniqlo@shop.com', '杭州市上城区延安路98号', '13900139005');

-- =====================================================
-- 2. 插入商品数据 (online_shopping_commodity)
-- =====================================================

-- 小米旗舰店的商品（creator_user_id = 11）
INSERT INTO online_shopping_commodity 
    (commodity_name, commodity_desc, price, total_stock, lock_stock, available_stock, creator_user_id) 
VALUES
('小米14 Pro 智能手机', '骁龙8 Gen3处理器，徕卡光学镜头，120W澎湃秒充', 499900, 500, 0, 500, 11),
('小米手环8 NFC版', '1.62英寸AMOLED屏幕，16天续航，150+运动模式', 29900, 1000, 0, 1000, 11),
('Redmi K70 Pro', '第三代骁龙8旗舰芯片，2K高光护眼屏，5000mAh电池', 329900, 800, 50, 750, 11),
('小米平板6 Max', '14英寸超大屏，骁龙8+旗舰芯片，8扬声器音响系统', 299900, 300, 20, 280, 11),
('小米笔记本Pro 14', '12代英特尔酷睿i5处理器，2.8K OLED屏幕，16GB内存', 549900, 200, 10, 190, 11);

-- 华为官方店的商品（creator_user_id = 12）
INSERT INTO online_shopping_commodity 
    (commodity_name, commodity_desc, price, total_stock, lock_stock, available_stock, creator_user_id) 
VALUES
('华为Mate 60 Pro', '麒麟芯片回归，昆仑玻璃，卫星通信', 699900, 600, 30, 570, 12),
('华为FreeBuds Pro 3', '超感知音质，智慧动态降噪3.0，IP54防尘防水', 139900, 1500, 100, 1400, 12),
('华为Watch GT 4', '高精准健康监测，两周长续航，100+运动模式', 148800, 800, 0, 800, 12),
('华为MateBook X Pro', '3.1K原色触控屏，第13代英特尔酷睿处理器', 899900, 150, 5, 145, 12),
('华为智能眼镜', '开放式双扬声器，反向充电，IP54防水', 199900, 400, 20, 380, 12);

-- 苹果授权店的商品（creator_user_id = 13）
INSERT INTO online_shopping_commodity 
    (commodity_name, commodity_desc, price, total_stock, lock_stock, available_stock, creator_user_id) 
VALUES
('iPhone 15 Pro Max 256GB', 'A17 Pro芯片，钛金属设计，灵动岛', 999900, 300, 50, 250, 13),
('AirPods Pro 2代', '主动降噪，空间音频，MagSafe充电盒', 189900, 2000, 150, 1850, 13),
('Apple Watch Series 9', '血氧检测，心电图，车祸检测，防水50米', 329900, 500, 30, 470, 13),
('iPad Air 11英寸', 'M2芯片，液态视网膜显示屏，支持Apple Pencil', 469900, 400, 25, 375, 13),
('MacBook Air 15英寸', 'M3芯片，15.3英寸液态视网膜显示屏，18小时续航', 1099900, 100, 8, 92, 13);

-- 耐克运动专营店的商品（creator_user_id = 14）
INSERT INTO online_shopping_commodity 
    (commodity_name, commodity_desc, price, total_stock, lock_stock, available_stock, creator_user_id) 
VALUES
('Nike Air Max 270 跑鞋', '全掌气垫，透气网面，轻量化设计', 129900, 1200, 80, 1120, 14),
('Nike Dri-FIT 运动T恤', '速干面料，吸湿排汗，多色可选', 19900, 3000, 200, 2800, 14),
('Nike Air Jordan 1 篮球鞋', '经典OG配色，全粒面皮革，耐克气垫', 169900, 800, 100, 700, 14),
('Nike Pro 紧身裤', '弹力面料，贴身剪裁，适合训练和跑步', 29900, 2000, 50, 1950, 14),
('Nike 运动背包', '大容量设计，多功能隔层，防水材质', 39900, 1500, 0, 1500, 14);

-- 优衣库旗舰店的商品（creator_user_id = 15）
INSERT INTO online_shopping_commodity 
    (commodity_name, commodity_desc, price, total_stock, lock_stock, available_stock, creator_user_id) 
VALUES
('HEATTECH 保暖内衣', '吸湿发热技术，轻薄保暖，抗静电', 7900, 5000, 300, 4700, 15),
('摇粒绒外套', '柔软舒适，保暖透气，多色可选', 14900, 3000, 150, 2850, 15),
('特级轻型羽绒服', '90%白鸭绒填充，超轻便携，防泼水', 39900, 2000, 100, 1900, 15),
('弹力牛仔裤', '高弹力面料，舒适修身，耐磨耐洗', 19900, 4000, 200, 3800, 15),
('AIRism T恤', 'AIRism面料，凉感透气，快干防臭', 5900, 6000, 400, 5600, 15);

-- =====================================================
-- 3. 插入订单数据 (online_shopping_order)
-- =====================================================

-- 已完成的订单（order_status = 3）
INSERT INTO online_shopping_order 
    (order_no, order_status, commodity_id, user_id, order_amount, create_time, pay_time) 
VALUES
('ORD2024010100001', 3, 1, 1, 499900, '2024-01-01 10:30:00', '2024-01-01 10:35:22'),
('ORD2024010100002', 3, 6, 2, 699900, '2024-01-01 14:20:15', '2024-01-01 14:25:30'),
('ORD2024010200003', 3, 11, 3, 999900, '2024-01-02 09:15:30', '2024-01-02 09:20:45'),
('ORD2024010200004', 3, 16, 4, 129900, '2024-01-02 16:45:20', '2024-01-02 16:50:10'),
('ORD2024010300005', 3, 21, 5, 7900, '2024-01-03 11:30:00', '2024-01-03 11:35:15');

-- 已支付的订单（order_status = 1）
INSERT INTO online_shopping_order 
    (order_no, order_status, commodity_id, user_id, order_amount, create_time, pay_time) 
VALUES
('ORD2024010300006', 1, 2, 6, 29900, '2024-01-03 13:20:30', '2024-01-03 13:25:45'),
('ORD2024010400007', 1, 7, 7, 139900, '2024-01-04 10:15:20', '2024-01-04 10:20:30'),
('ORD2024010400008', 1, 12, 8, 189900, '2024-01-04 15:30:10', '2024-01-04 15:35:25'),
('ORD2024010500009', 1, 17, 9, 19900, '2024-01-05 09:45:30', '2024-01-05 09:50:15'),
('ORD2024010500010', 1, 22, 10, 14900, '2024-01-05 14:20:45', '2024-01-05 14:25:30');

-- 待支付的订单（order_status = 0）
INSERT INTO online_shopping_order 
    (order_no, order_status, commodity_id, user_id, order_amount, create_time, pay_time) 
VALUES
('ORD2024010600011', 0, 3, 1, 329900, '2024-01-06 10:30:00', NULL),
('ORD2024010600012', 0, 8, 2, 148800, '2024-01-06 11:45:20', NULL),
('ORD2024010600013', 0, 13, 3, 329900, '2024-01-06 13:20:30', NULL),
('ORD2024010700014', 0, 18, 4, 169900, '2024-01-07 09:15:40', NULL),
('ORD2024010700015', 0, 23, 5, 39900, '2024-01-07 10:30:50', NULL);

-- 已取消的订单（order_status = 2）
INSERT INTO online_shopping_order 
    (order_no, order_status, commodity_id, user_id, order_amount, create_time, pay_time) 
VALUES
('ORD2024010200016', 2, 4, 6, 299900, '2024-01-02 15:30:00', NULL),
('ORD2024010300017', 2, 9, 7, 199900, '2024-01-03 16:45:20', NULL),
('ORD2024010400018', 2, 14, 8, 469900, '2024-01-04 11:20:30', NULL);

-- =====================================================
-- 4. 数据统计信息
-- =====================================================

-- 统计用户数量
SELECT 
    user_type,
    CASE 
        WHEN user_type = 0 THEN '普通用户'
        WHEN user_type = 1 THEN '商家用户'
    END AS user_type_name,
    COUNT(*) AS user_count
FROM online_shopping_user
GROUP BY user_type;

-- 统计商品数量和库存
SELECT 
    COUNT(*) AS total_commodities,
    SUM(total_stock) AS total_stock_sum,
    SUM(available_stock) AS available_stock_sum,
    SUM(lock_stock) AS lock_stock_sum
FROM online_shopping_commodity;

-- 统计订单数量和金额
SELECT 
    order_status,
    CASE 
        WHEN order_status = 0 THEN '待支付'
        WHEN order_status = 1 THEN '已支付'
        WHEN order_status = 2 THEN '已取消'
        WHEN order_status = 3 THEN '已完成'
    END AS order_status_name,
    COUNT(*) AS order_count,
    SUM(order_amount) AS total_amount
FROM online_shopping_order
GROUP BY order_status;

-- =====================================================
-- 完成提示
-- =====================================================
SELECT 'Test data inserted successfully!' AS status;
SELECT 'Total Users:' AS info, COUNT(*) AS count FROM online_shopping_user
UNION ALL
SELECT 'Total Commodities:', COUNT(*) FROM online_shopping_commodity
UNION ALL
SELECT 'Total Orders:', COUNT(*) FROM online_shopping_order;