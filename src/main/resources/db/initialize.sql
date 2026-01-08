-- =====================================================
-- 在线购物系统数据库初始化脚本
-- 第一部分：清空并重建数据库和表结构
-- =====================================================

-- 1. 删除旧数据库（如果存在）
DROP DATABASE IF EXISTS online_shopping_db;

-- 2. 创建新数据库
CREATE DATABASE online_shopping_db 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 3. 使用数据库
USE online_shopping_db;

-- =====================================================
-- 创建表：用户表 (online_shopping_user)
-- =====================================================
CREATE TABLE online_shopping_user (
    user_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    user_type INT NOT NULL DEFAULT 0 COMMENT '用户类型：0-普通用户, 1-商家',
    name VARCHAR(100) NOT NULL COMMENT '用户姓名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    address VARCHAR(255) DEFAULT NULL COMMENT '地址',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_email (email),
    KEY idx_user_type (user_type),
    KEY idx_phone (phone)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 创建表：商品表 (online_shopping_commodity)
-- =====================================================
CREATE TABLE online_shopping_commodity (
    commodity_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    commodity_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    commodity_desc TEXT DEFAULT NULL COMMENT '商品描述',
    price INT NOT NULL DEFAULT 0 COMMENT '商品价格（单位：分）',
    available_stock INT NOT NULL DEFAULT 0 COMMENT '可用库存',
    total_stock INT NOT NULL DEFAULT 0 COMMENT '总库存',
    lock_stock INT NOT NULL DEFAULT 0 COMMENT '锁定库存（订单未支付）',
    creator_user_id BIGINT NOT NULL COMMENT '创建者用户ID（商家ID）',
    PRIMARY KEY (commodity_id),
    KEY idx_creator (creator_user_id),
    KEY idx_price (price),
    KEY idx_available_stock (available_stock),
    CONSTRAINT fk_commodity_creator FOREIGN KEY (creator_user_id) 
        REFERENCES online_shopping_user(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- =====================================================
-- 创建表：订单表 (online_shopping_order)
-- =====================================================
CREATE TABLE online_shopping_order (
    order_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(64) NOT NULL COMMENT '订单号',
    order_status INT NOT NULL DEFAULT 0 COMMENT '订单状态：0-待支付, 1-已支付, 2-已取消, 3-已完成',
    commodity_id BIGINT NOT NULL COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_amount BIGINT NOT NULL DEFAULT 0 COMMENT '订单金额（单位：分）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_commodity_id (commodity_id),
    KEY idx_order_status (order_status),
    KEY idx_create_time (create_time),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) 
        REFERENCES online_shopping_user(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_order_commodity FOREIGN KEY (commodity_id) 
        REFERENCES online_shopping_commodity(commodity_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =====================================================
-- 创建索引说明
-- =====================================================
-- user_id: 主键自增
-- email: 唯一索引，用于登录验证
-- phone: 普通索引，用于快速查找
-- commodity_id: 主键自增
-- creator_user_id: 外键索引，关联商家
-- order_no: 唯一索引，订单号不可重复
-- order_status: 普通索引，用于订单状态筛选

-- =====================================================
-- 完成提示
-- =====================================================
SELECT 'Database and tables created successfully!' AS status;