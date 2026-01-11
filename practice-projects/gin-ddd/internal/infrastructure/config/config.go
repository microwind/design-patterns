package config

import (
	"fmt"
	"log"
	"os"

	"gopkg.in/yaml.v3"
)

// AppConfig 应用配置
type AppConfig struct {
	Server   ServerConfig   `yaml:"server"`
	Database DatabaseInfo   `yaml:"database"`
	Logger   LoggerConfig   `yaml:"logger"`
	RocketMQ RocketMQConfig `yaml:"rocketmq"`
}

// ServerConfig 服务器配置
type ServerConfig struct {
	Host string `yaml:"host"`
	Port int    `yaml:"port"`
	Mode string `yaml:"mode"` // debug, release, test
}

// DatabaseInfo 数据库信息
type DatabaseInfo struct {
	Driver          string `yaml:"driver"`
	Host            string `yaml:"host"`
	Port            int    `yaml:"port"`
	Username        string `yaml:"username"`
	Password        string `yaml:"password"`
	Database        string `yaml:"database"`
	MaxOpenConns    int    `yaml:"max_open_conns"`
	MaxIdleConns    int    `yaml:"max_idle_conns"`
	ConnMaxLifetime int    `yaml:"conn_max_lifetime"` // 秒
}

// LoggerConfig 日志配置
type LoggerConfig struct {
	Level  string `yaml:"level"`  // debug, info, warn, error
	Format string `yaml:"format"` // json, text
}

// RocketMQConfig RocketMQ 配置
type RocketMQConfig struct {
	Enabled      bool              `yaml:"enabled"`
	NameServer   string            `yaml:"nameserver"`
	GroupName    string            `yaml:"group_name"`
	InstanceName string            `yaml:"instance_name"`
	RetryTimes   int               `yaml:"retry_times"`
	Topics       map[string]string `yaml:"topics"`
}

var config *AppConfig

// LoadConfig 加载配置文件
func LoadConfig(configPath string) (*AppConfig, error) {
	data, err := os.ReadFile(configPath)
	if err != nil {
		return nil, fmt.Errorf("读取配置文件失败: %w", err)
	}

	var cfg AppConfig
	if err := yaml.Unmarshal(data, &cfg); err != nil {
		return nil, fmt.Errorf("解析配置文件失败: %w", err)
	}

	config = &cfg
	log.Printf("配置文件加载成功: %s", configPath)
	return &cfg, nil
}

// GetConfig 获取配置
func GetConfig() *AppConfig {
	if config == nil {
		log.Fatal("配置未初始化，请先调用 LoadConfig")
	}
	return config
}
