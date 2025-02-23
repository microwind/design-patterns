# -*- coding: utf-8 -*-
import os

# 服务器端口
PORT = int(os.getenv('PORT', 8080))

# 环境变量
ENV = os.getenv('NODE_ENV', 'development')

# 数据库配置
DATABASE = {
    'host': os.getenv('DB_HOST', 'localhost'),
    'port': int(os.getenv('DB_PORT', 5432)),
    'user': os.getenv('DB_USER', 'postgres'),
    'password': os.getenv('DB_PASSWORD', 'password'),
    'name': os.getenv('DB_NAME', 'order_db')
}

# 日志配置
LOGGING = {
    'level': os.getenv('LOG_LEVEL', 'info'),
    'file': os.getenv('LOG_FILE', 'app.log')
}

# JWT 配置
JWT = {
    'secret': os.getenv('JWT_SECRET', 'your_jwt_secret'),
    'expires_in': os.getenv('JWT_EXPIRES_IN', '1h')
}

# 其他配置
APP_NAME = os.getenv('APP_NAME', 'DDD Node.js App')