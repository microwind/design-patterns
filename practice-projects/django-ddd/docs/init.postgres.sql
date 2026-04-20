-- ==================================================
-- django-ddd PostgreSQL 初始化脚本（订单库：seed）
--
-- 用法：
--   A) 全新环境（需要自己建库）：
--        psql -U postgres -c "CREATE DATABASE seed;"
--        psql -U postgres -d seed -f docs/init.postgres.sql
--   B) docker compose 启动时自动执行（/docker-entrypoint-initdb.d）
-- ==================================================

DROP TABLE IF EXISTS orders CASCADE;

CREATE TABLE orders (
    id            BIGSERIAL      PRIMARY KEY,
    order_no      VARCHAR(50)    NOT NULL UNIQUE,
    user_id       BIGINT         NOT NULL,
    total_amount  DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    status        VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status  ON orders (status);

COMMENT ON TABLE  orders         IS '订单表';
COMMENT ON COLUMN orders.status  IS 'PENDING / PAID / SHIPPED / DELIVERED / CANCELLED / REFUNDED';

-- 可选：测试数据
INSERT INTO orders (order_no, user_id, total_amount, status) VALUES
    ('ORD202601090001', 1, 199.99, 'PENDING'),
    ('ORD202601090002', 2, 299.99, 'PAID');
