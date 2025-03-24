package config

import (
  "os"
  "strconv"
)

// ServerConfig 服务器配置结构体
type ServerConfig struct {
  Port     int    // 服务器端口
  Env      string // 环境变量（开发、生产、测试）
  Database DatabaseConfig
  Logging  LoggingConfig
  JWT      JWTConfig
  AppName  string // 应用名称
}

// DatabaseConfig 数据库配置
type DatabaseConfig struct {
  Host     string
  Port     int
  User     string
  Password string
  Name     string
}

// LoggingConfig 日志配置
type LoggingConfig struct {
  Level string
  File  string
}

// JWTConfig JWT 配置
type JWTConfig struct {
  Secret    string
  ExpiresIn string
}

// NewServerConfig 创建并返回服务器配置
func NewServerConfig() *ServerConfig {
  return &ServerConfig{
    Port: getEnvAsInt("PORT", 8080),
    Env:  getEnv("NODE_ENV", "development"),
    Database: DatabaseConfig{
      Host:     getEnv("DB_HOST", "localhost"),
      Port:     getEnvAsInt("DB_PORT", 5432),
      User:     getEnv("DB_USER", "postgres"),
      Password: getEnv("DB_PASSWORD", "password"),
      Name:     getEnv("DB_NAME", "order_db"),
    },
    Logging: LoggingConfig{
      Level: getEnv("LOG_LEVEL", "info"),
      // 定义日志文件地址
      File: getEnv("LOG_FILE", "logs/app.log"),
    },
    JWT: JWTConfig{
      Secret:    getEnv("JWT_SECRET", "your_jwt_secret"),
      ExpiresIn: getEnv("JWT_EXPIRES_IN", "1h"),
    },
    AppName: getEnv("APP_NAME", "DDD Go App"),
  }
}

// getEnv 获取环境变量值，默认返回指定的默认值
func getEnv(key, defaultValue string) string {
  if value, exists := os.LookupEnv(key); exists {
    return value
  }
  return defaultValue
}

// getEnvAsInt 获取环境变量值并转换为整数，若不存在则返回默认值
func getEnvAsInt(key string, defaultValue int) int {
  value := getEnv(key, "")
  if value == "" {
    return defaultValue
  }

  parsedValue, err := strconv.Atoi(value)
  if err != nil {
    return defaultValue
  }
  return parsedValue
}
