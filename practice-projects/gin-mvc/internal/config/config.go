package config

import (
	"fmt"
	"os"

	"gopkg.in/yaml.v3"
)

type AppConfig struct {
	Server   ServerConfig   `yaml:"server"`
	Database DatabaseGroup  `yaml:"database"`
	Log      LogConfig      `yaml:"log"`
	RocketMQ RocketMQConfig `yaml:"rocketmq"`
	Mail     MailConfig     `yaml:"mail"`
}

type ServerConfig struct {
	Host                string `yaml:"host"`
	Port                int    `yaml:"port"`
	Mode                string `yaml:"mode"`
	ReadTimeoutSeconds  int    `yaml:"read_timeout_seconds"`
	WriteTimeoutSeconds int    `yaml:"write_timeout_seconds"`
	IdleTimeoutSeconds  int    `yaml:"idle_timeout_seconds"`
}

type DatabaseGroup struct {
	User  DatabaseInfo `yaml:"user"`
	Order DatabaseInfo `yaml:"order"`
}

type DatabaseInfo struct {
	Driver                 string `yaml:"driver"`
	Host                   string `yaml:"host"`
	Port                   int    `yaml:"port"`
	UserName               string `yaml:"username"`
	Password               string `yaml:"password"`
	Database               string `yaml:"database"`
	MaxOpenConns           int    `yaml:"max_open_conns"`
	MaxIdleConns           int    `yaml:"max_idle_conns"`
	ConnMaxLifetimeSeconds int    `yaml:"conn_max_lifetime_seconds"`
}

type LogConfig struct {
	Level  string `yaml:"level"`
	Format string `yaml:"format"`
}

type RocketMQConfig struct {
	Enabled      bool              `yaml:"enabled"`
	NameServer   string            `yaml:"nameserver"`
	GroupName    string            `yaml:"group_name"`
	InstanceName string            `yaml:"instance_name"`
	RetryTimes   int               `yaml:"retry_times"`
	Topics       map[string]string `yaml:"topics"`
}

type MailConfig struct {
	Enabled   bool   `yaml:"enabled"`
	Host      string `yaml:"host"`
	Port      int    `yaml:"port"`
	Username  string `yaml:"username"`
	Password  string `yaml:"password"`
	FromEmail string `yaml:"from_email"`
	FromName  string `yaml:"from_name"`
}

func Load(path string) (*AppConfig, error) {
	data, err := os.ReadFile(path)
	if err != nil {
		return nil, fmt.Errorf("read config failed: %w", err)
	}

	var cfg AppConfig
	if err := yaml.Unmarshal(data, &cfg); err != nil {
		return nil, fmt.Errorf("unmarshal config failed: %w", err)
	}

	if err := cfg.Validate(); err != nil {
		return nil, err
	}

	return &cfg, nil
}

func (c *AppConfig) Validate() error {
	if c.Server.Port <= 0 {
		return fmt.Errorf("server.port must be greater than 0")
	}
	if c.Database.User.Driver == "" || c.Database.Order.Driver == "" {
		return fmt.Errorf("database.user.driver and database.order.driver are required")
	}
	if c.RocketMQ.Enabled {
		if c.RocketMQ.NameServer == "" {
			return fmt.Errorf("rocketmq.nameserver is required when rocketmq.enabled=true")
		}
		if c.RocketMQ.Topics["order_event"] == "" {
			return fmt.Errorf("rocketmq.topics.order_event is required when rocketmq.enabled=true")
		}
	}
	if c.Mail.Enabled {
		if c.Mail.Host == "" || c.Mail.Username == "" || c.Mail.Password == "" || c.Mail.FromEmail == "" {
			return fmt.Errorf("mail host/username/password/from_email are required when mail.enabled=true")
		}
	}
	return nil
}
