-- 创建用户表
-- 使用此脚本在 frog 数据库中创建 users 表

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    wechat VARCHAR(100) COMMENT '微信',
    address VARCHAR(255) COMMENT '详细地址'
    created_time BIGINT NOT NULL COMMENT '创建时间',
    updated_time BIGINT NOT NULL COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE INDEX idx_name (name),
    UNIQUE INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据
INSERT INTO user (name, email, phone, created_time, updated_time) VALUES
('admin', 'admin@example.com', '13800138000', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
('user1', 'user1@example.com', '13800138001', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000),
('user2', 'user2@example.com', '13800138002', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000);
