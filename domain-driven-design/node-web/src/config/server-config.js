// src/config/server-config.js

export default {
  // 服务器端口
  port: process.env.PORT || 8080, // 默认端口为 8080

  // 环境变量
  env: process.env.NODE_ENV || 'development', // 当前环境（development、production、test）

  // 数据库配置
  database: {
    host: process.env.DB_HOST || 'localhost', // 数据库主机
    port: process.env.DB_PORT || 5432, // 数据库端口
    user: process.env.DB_USER || 'postgres', // 数据库用户名
    password: process.env.DB_PASSWORD || 'password', // 数据库密码
    name: process.env.DB_NAME || 'order_db', // 数据库名称
  },

  // 日志配置
  logging: {
    level: process.env.LOG_LEVEL || 'info', // 日志级别（debug、info、warn、error）
    file: process.env.LOG_FILE || 'app.log', // 日志文件路径
  },

  // JWT 配置
  jwt: {
    secret: process.env.JWT_SECRET || 'your_jwt_secret', // JWT 密钥
    expiresIn: process.env.JWT_EXPIRES_IN || '1h', // JWT 过期时间
  },

  // 其他配置
  appName: process.env.APP_NAME || 'DDD Node.js App', // 应用名称
};