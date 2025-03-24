/**
 * ConnectDatabase 数据库连接器
 * @author jarryli@gmail.com
 */
package repository

import (
  "database/sql"
  "fmt"
  "gin-order/pkg/logger"
  "net/url"
  "os"
  "time"

  _ "github.com/go-sql-driver/mysql"
)

// Duration 是一个可以解析 YAML 时间字符串的类型
type Duration time.Duration

type DatabaseConfig struct {
  Host            string
  Port            int
  User            string
  Password        string
  DBName          string
  MaxOpenConns    int    // 最大打开连接数 (建议值: 25)
  MaxIdleConns    int    // 最大空闲连接数 (建议值: 25)
  ConnMaxLifetime string // 连接最大生命周期 (建议值: 5m)
  TimeZone        string // 时区配置 (示例: "Asia/Shanghai")
  SchemaFile      string // schema.sql 文件路径
}

func ConnectMySQL(dbConfig *DatabaseConfig) (*sql.DB, error) {
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
    "%s:%s@tcp(%s:%d)/%s?parseTime=true&charset=utf8mb4&collation=utf8mb4_unicode_ci&loc=%s&multiStatements=true",
    encodedUser,
    encodedPassword,
    dbConfig.Host,
    dbConfig.Port,
    dbConfig.DBName,
    url.QueryEscape(timeZone), // 时区需要二次编码
  )

  // 连接数据库
  db, err := sql.Open("mysql", dsn)
  if err != nil {
    logger.Printf("无法连接到数据库，错误信息：%v", err)
    return nil, fmt.Errorf("无法连接到数据库: %w", err)
  }

  // 读取文件内容
  if dbConfig.SchemaFile != "" {
    content, err := os.ReadFile(dbConfig.SchemaFile)
    if err != nil {
      return nil, fmt.Errorf("failed to read schema file: %w", err)
    }

    // 将读取到的内容转换为字符串后执行
    sql := string(content)
    _, err = db.Exec(sql)
    if err != nil {
      return nil, fmt.Errorf("failed to execute schema file: %w", err)
    }
  }

  // 配置连接池（带默认值逻辑）
  if dbConfig.MaxOpenConns > 0 {
    db.SetMaxOpenConns(dbConfig.MaxOpenConns)
  } else {
    db.SetMaxOpenConns(25) // 默认值
  }

  if dbConfig.MaxIdleConns > 0 {
    db.SetMaxIdleConns(dbConfig.MaxIdleConns)
  } else {
    db.SetMaxIdleConns(25) // 默认值
  }

  connMaxLifetime, _ := time.ParseDuration(dbConfig.ConnMaxLifetime)
  if connMaxLifetime > 0 {
    db.SetConnMaxLifetime(connMaxLifetime)
  } else {
    db.SetConnMaxLifetime(5 * time.Minute) // 默认值
  }

  // 验证连接有效性
  if err := db.Ping(); err != nil {
    logger.Printf("数据库不可用，错误信息：%v", err)
    _ = db.Close() // 显式关闭无效连接
    return nil, fmt.Errorf("数据库不可用: %w", err)
  }

  logger.Println("ConnectMySQL successfully. 成功连接到数据库。")
  return db, nil
}
