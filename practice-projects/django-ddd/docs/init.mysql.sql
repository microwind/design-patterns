-- ==================================================
-- django-ddd MySQL 初始化脚本（用户库：frog）
-- 与 gin-ddd / nestjs-ddd 完全对齐
--
-- 用法：
--   1) 本地：mysql -u root -p < docs/init.mysql.sql
--   2) docker compose 启动时会自动执行
-- ==================================================

CREATE DATABASE IF NOT EXISTS frog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE frog;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY                       COMMENT '用户ID',
    name          VARCHAR(50)  NOT NULL UNIQUE                            COMMENT '用户名',
    email         VARCHAR(100) NOT NULL UNIQUE                            COMMENT '邮箱',
    phone         VARCHAR(20)  NULL                                       COMMENT '手机号',
    address       VARCHAR(255) NULL                                       COMMENT '地址',
    created_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP         COMMENT '创建时间',
    updated_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP           COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 可选：测试数据
INSERT INTO users (name, email, phone, address) VALUES
    ('admin', 'admin@example.com', '13800000000', '北京'),
    ('user1', 'user1@example.com', '13800000001', '上海'),
    ('user2', 'user2@example.com', '13800000002', '广州');
