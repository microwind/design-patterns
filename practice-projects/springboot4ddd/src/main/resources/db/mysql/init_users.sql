-- MySQL数据库初始化脚本
-- 用于创建用户表

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS frog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE frog;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试数据
INSERT INTO users (username, email, phone, nickname, status) VALUES
('user1', 'user1@example.com', '13800138001', '测试用户1', 1),
('user2', 'user2@example.com', '13800138002', '测试用户2', 1),
('user3', 'user3@example.com', '13800138003', '测试用户3', 0)
ON DUPLICATE KEY UPDATE username=username;
