-- 本文件仅为示例，实际请根据需要来建立和导入库表
CREATE DATABASE order_db;
use order_db;
-- orders表
CREATE TABLE `orders` (
  `order_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `order_name` varchar(255) NOT NULL COMMENT '订单名称',
  `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'CREATED' COMMENT '订单状态',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=280 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';

-- order_item表
CREATE TABLE `order_item` (
  `order_item_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `product` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL,
  `order_id` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`order_item_id`),
  KEY `FKt4dc2r9nbvbujrljv3e23iibt` (`order_id`),
  CONSTRAINT `FKt4dc2r9nbvbujrljv3e23iibt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- users表示例
CREATE DATABASE frog;
use frog;
-- users表
CREATE TABLE `users` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `wechat` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text COLLATE utf8mb4_unicode_ci,
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_email` (`email`),
  KEY `idx_name` (`name`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CREATE TABLE users (\n    id INT AUTO_INCREMENT PRIMARY KEY,        -- 用户ID，自增\n    name VARCHAR(100) NOT NULL,               -- 姓名\n    email VARCHAR(100) NOT NULL UNIQUE,       -- 邮箱，设置唯一约束\n    phone VARCHAR(20) NOT NULL UNIQUE,        -- 手机号，设置唯一约束\n    wechat VARCHAR(100),                   -- 微信号\n    address TEXT,                             -- 地址\n    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 创建时间\n    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间\n);\n\n-- 添加索引\nCREATE INDEX idx_phone ON users (phone);      -- 手机索引\nCREATE INDEX idx_email ON users (email);      -- 邮箱索引\nCREATE INDEX idx_name ON users (name);        -- 姓名索引';

-- apiauth表示例，采用postgresql