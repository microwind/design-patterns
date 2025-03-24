
-- 此处仅为示意，数据库提前创建好。
use order_db;
show tables;
-- CREATE TABLE `orders` (
--   `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
--   `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
--   `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
--   `order_name` VARCHAR(255) NOT NULL COMMENT '订单名称',
--   `amount` DECIMAL(10, 2) NOT NULL COMMENT '订单金额',
--   `status` VARCHAR(50) NOT NULL COMMENT '订单状态',
--   `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   PRIMARY KEY (`order_id`),
--   UNIQUE KEY `idx_order_no` (`order_no`),
--   KEY `idx_user_id` (`user_id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';