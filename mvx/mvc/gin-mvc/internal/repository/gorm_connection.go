/**
 * ConnectDatabase 数据库连接器 (基于 GORM)
 * @author jarryli@gmail.com
 */
package repository

import (
  "fmt"
  "net/url"
  "time"

  "gorm.io/driver/mysql"
  "gorm.io/gorm"
  "gorm.io/gorm/logger"
)

func ConnectDatabase(dbConfig *DatabaseConfig) (*gorm.DB, error) {
  if dbConfig == nil {
    return nil, fmt.Errorf("数据库配置不能为空")
  }

  // 对用户名密码进行URL编码，处理特殊字符
  encodedUser := url.QueryEscape(dbConfig.User)
  encodedPassword := url.QueryEscape(dbConfig.Password)

  // 构建时区参数（默认Local）
  timeZone := dbConfig.TimeZone
  if timeZone == "" {
    timeZone = "Local"
  }

  // 构建DSN连接字符串
  dsn := fmt.Sprintf(
    "%s:%s@tcp(%s:%d)/%s?parseTime=true&charset=utf8mb4&collation=utf8mb4_unicode_ci&loc=%s",
    encodedUser,
    encodedPassword,
    dbConfig.Host,
    dbConfig.Port,
    dbConfig.DBName,
    url.QueryEscape(timeZone), // 时区需要二次编码
  )

  // 初始化 GORM 数据库连接
  gormDB, err := gorm.Open(mysql.Open(dsn), &gorm.Config{
    Logger: logger.Default.LogMode(logger.Info), // 设置日志模式为 Info（可选 Silent、Warn、Error）
  })
  if err != nil {
    return nil, fmt.Errorf("无法连接到数据库: %w", err)
  }

  // 获取底层的 sql.DB 实例以配置连接池
  sqlDB, err := gormDB.DB()
  if err != nil {
    return nil, fmt.Errorf("无法获取底层数据库实例: %w", err)
  }

  // 配置连接池（带默认值逻辑）
  if dbConfig.MaxOpenConns > 0 {
    sqlDB.SetMaxOpenConns(dbConfig.MaxOpenConns)
  } else {
    sqlDB.SetMaxOpenConns(25) // 默认值
  }

  if dbConfig.MaxIdleConns > 0 {
    sqlDB.SetMaxIdleConns(dbConfig.MaxIdleConns)
  } else {
    sqlDB.SetMaxIdleConns(25) // 默认值
  }

  connMaxLifetime, _ := time.ParseDuration(dbConfig.ConnMaxLifetime)
  if connMaxLifetime > 0 {
    sqlDB.SetConnMaxLifetime(connMaxLifetime)
  } else {
    sqlDB.SetConnMaxLifetime(5 * time.Minute) // 默认值
  }

  // 验证连接有效性
  if err := sqlDB.Ping(); err != nil {
    return nil, fmt.Errorf("数据库不可用: %w", err)
  }

  fmt.Println("成功连接到数据库")
  return gormDB, nil
}
