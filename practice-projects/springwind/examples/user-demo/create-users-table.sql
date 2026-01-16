-- 创建 users 表（用于 user-demo 项目）
-- 使用此脚本在 frog 数据库中创建 users 表

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    name VARCHAR(100) NOT NULL COMMENT '用户名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    created_time BIGINT NOT NULL COMMENT '创建时间（时间戳）',
    updated_time BIGINT NOT NULL COMMENT '更新时间（时间戳）',
    PRIMARY KEY (id),
--    UNIQUE KEY unique_name (name) COMMENT '用户名唯一约束',
    UNIQUE KEY unique_email (email) COMMENT '邮箱唯一约束',
    UNIQUE KEY unique_phone (phone) COMMENT '手机号唯一约束',
    INDEX idx_name (name),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 说明：
-- 1. 三个唯一约束：name, email, phone
-- 2. created_time 和 updated_time 使用毫秒时间戳（Long类型）
-- 3. 如果不需要手机号唯一约束，可以删除 unique_phone 约束
