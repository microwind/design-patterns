-- PostgreSQL数据库初始化脚本
-- 用于创建订单表

-- 创建数据库（如果不存在，需要在psql命令行中执行）
-- CREATE DATABASE seed;

-- 连接到seed数据库后执行以下SQL

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_orders_order_no ON orders(order_no);
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);

-- 添加注释
COMMENT ON TABLE orders IS '订单表';
COMMENT ON COLUMN orders.id IS '订单ID';
COMMENT ON COLUMN orders.order_no IS '订单号';
COMMENT ON COLUMN orders.user_id IS '用户ID';
COMMENT ON COLUMN orders.total_amount IS '订单总金额';
COMMENT ON COLUMN orders.status IS '订单状态：PENDING-待支付，PAID-已支付，CANCELLED-已取消，COMPLETED-已完成';
COMMENT ON COLUMN orders.created_at IS '创建时间';
COMMENT ON COLUMN orders.updated_time IS '更新时间';

-- 插入测试数据
INSERT INTO orders (order_no, user_id, total_amount, status) VALUES
('ORD1000000001', 1, 99.99, 'PENDING'),
('ORD1000000002', 1, 199.99, 'PAID'),
('ORD1000000003', 2, 299.99, 'COMPLETED')
ON CONFLICT (order_no) DO NOTHING;
