package config

import (
	"database/sql"
	"fmt"
	"gin-ddd/pkg/utils"
	"time"

	_ "github.com/go-sql-driver/mysql"
	_ "github.com/lib/pq"
)

// DatabaseConfig 数据库配置
type DatabaseConfig struct {
	Driver          string
	Host            string
	Port            int
	UserName        string
	Password        string
	Database        string
	MaxOpenConns    int
	MaxIdleConns    int
	ConnMaxLifetime time.Duration
}

// InitDatabase 初始化数据库连接
func InitDatabase(config *DatabaseConfig) (*sql.DB, error) {
	var dsn string

	utils.GetLogger().Info("初始化%s数据库: host=%s, port=%d, database=%s", config.Driver, config.Host, config.Port, config.Database)

	switch config.Driver {
	case "mysql":
		dsn = fmt.Sprintf("%s:%s@tcp(%s:%d)/%s?charset=utf8mb4&parseTime=True&loc=Local",
			config.UserName,
			config.Password,
			config.Host,
			config.Port,
			config.Database,
		)
	case "postgres":
		dsn = fmt.Sprintf("host=%s port=%d user=%s password=%s dbname=%s sslmode=disable",
			config.Host,
			config.Port,
			config.UserName,
			config.Password,
			config.Database,
		)
	default:
		utils.GetLogger().Error("不支持的数据库驱动: %s", config.Driver)
		return nil, fmt.Errorf("不支持的数据库驱动: %s", config.Driver)
	}

	db, err := sql.Open(config.Driver, dsn)
	if err != nil {
		utils.GetLogger().Error("打开数据库连接失败: %v, driver=%s, host=%s", err, config.Driver, config.Host)
		return nil, fmt.Errorf("打开数据库连接失败: %w", err)
	}

	// 设置连接池参数
	db.SetMaxOpenConns(config.MaxOpenConns)
	db.SetMaxIdleConns(config.MaxIdleConns)
	db.SetConnMaxLifetime(config.ConnMaxLifetime)
	utils.GetLogger().Debug("数据库连接池配置: MaxOpenConns=%d, MaxIdleConns=%d, ConnMaxLifetime=%v",
		config.MaxOpenConns, config.MaxIdleConns, config.ConnMaxLifetime)

	// 测试连接
	if err := db.Ping(); err != nil {
		utils.GetLogger().Error("数据库连接测试失败: %v, driver=%s, host=%s", err, config.Driver, config.Host)
		return nil, fmt.Errorf("数据库连接测试失败: %w", err)
	}

	utils.GetLogger().Info("数据库连接成功: %s@%s:%d/%s", config.Driver, config.Host, config.Port, config.Database)
	return db, nil
}
