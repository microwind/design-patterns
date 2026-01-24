-- MySQL 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS gin_ddd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE gin_ddd;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, INACTIVE, BLOCKED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING, PAID, SHIPPED, DELIVERED, CANCELLED, REFUNDED',
    items JSON NOT NULL COMMENT '订单项（JSON格式）',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入测试数据
INSERT INTO users (name, email, phone, status) VALUES
    ('admin', 'admin@example.com', '13800000000', 'ACTIVE'),
    ('user1', 'user1@example.com', '13800000001', 'ACTIVE'),
    ('user2', 'user2@example.com', '13800000002', 'INACTIVE');

INSERT INTO orders (order_no, user_id, total_amount, status, items) VALUES
    ('ORD202601090001', 1, 199.99, 'PENDING', '[{"product_id":1,"product_name":"商品A","quantity":2,"price":99.99,"subtotal":199.98}]'),
    ('ORD202601090002', 2, 299.99, 'PAID', '[{"product_id":2,"product_name":"商品B","quantity":1,"price":299.99,"subtotal":299.99}]');
