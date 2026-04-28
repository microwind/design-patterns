-- Initialize order database (PostgreSQL)

CREATE DATABASE IF NOT EXISTS flask_mvc_order;

\c flask_mvc_order;

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_id ON orders(user_id);
CREATE INDEX idx_order_no ON orders(order_no);
CREATE INDEX idx_status ON orders(status);
