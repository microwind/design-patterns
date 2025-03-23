package config

import (
	"fmt"
	"gin-order/pkg/logger"
	"os"
	"sync"
	"time"

	"gopkg.in/yaml.v2"
)

// GetEnv 获取环境变量
func GetEnv(key, fallback string) string {
  if value, exists := os.LookupEnv(key); exists {
    return value
  }
  return fallback
}


// AppConfig 定义配置结构
type Config struct {
	Server struct {
		Addr         string `yaml:"addr"`
    Port         string `yaml:"port"`
		ReadTimeout  string `yaml:"read_timeout"`
		WriteTimeout string `yaml:"write_timeout"`
		Env          string `yaml:"env"`
	} `yaml:"server"`

	Database struct {
		Host     string `yaml:"host"`
		Port     int    `yaml:"port"`
		User     string `yaml:"user"`
		Password string `yaml:"password"`
		DBName   string `yaml:"dbname"`
		MaxOpenConns    int           `yaml:"max_open_conns"`    // 最大打开连接数 (建议值: 25)
		MaxIdleConns    int           `yaml:"max_idle_conns"`    // 最大空闲连接数 (建议值: 25)
		ConnMaxLifetime string           `yaml:"conn_max_lifetime"` // 连接最大生命周期 (建议值: 5m)
		TimeZone        string        `yaml:"time_zone"`         // 时区配置 (示例: "Asia/Shanghai")
	} `yaml:"database"`

	Cache struct {
		Type  string `yaml:"type"`
		Redis struct {
			Addr     string `yaml:"addr"`
			Password string `yaml:"password"`
			DB       int    `yaml:"db"`
		} `yaml:"redis"`
		MaxSize    int `yaml:"max_size"`
		Expiration int `yaml:"expiration"`
	} `yaml:"cache"`

	Log struct {
		Level  string `yaml:"level"`
		Format string `yaml:"format"`
		Output string `yaml:"output"`
	} `yaml:"log"`
}

var (
	configInstance *Config
	configLock     sync.Mutex
)

// Duration 是一个可以解析 YAML 时间字符串的类型
type Duration time.Duration

// UnmarshalYAML 实现了 yaml.Unmarshaler 接口
func (d *Duration) UnmarshalYAML(unmarshal func(interface{}) error) error {
	var s string
	if err := unmarshal(&s); err != nil {
		return err
	}
	duration, err := time.ParseDuration(s)
	if err != nil {
		return err
	}
	*d = Duration(duration)
	return nil
}

// LoadConfig 从指定文件加载配置
func LoadConfig[T any](filePath string) (*T, error) {
	file, err := os.Open(filePath)
	if err != nil {
		return nil, fmt.Errorf("unable to open config file: %w", err)
	}
	defer file.Close()

	var cfg T
	decoder := yaml.NewDecoder(file)
	if err := decoder.Decode(&cfg); err != nil {
		return nil, fmt.Errorf("unable to decode config file: %w", err)
	}
	return &cfg, nil
}

// GetConfig 获取全局配置实例
func GetConfig() (*Config) {
	// 如果配置实例为空，则加载配置
	if configInstance == nil {
    env := "test"
    Init(&env)
	}
	return configInstance
}

func Init(env *string) {
	configLock.Lock()
	defer configLock.Unlock()

	// 如果配置实例为空，则加载配置
	if configInstance == nil {
		var filePath string
		if *env == "production" {
			filePath = "internal/config/config_prod.yaml"
		} else if *env == "test" {
			filePath = "internal/config/config_test.yaml"
		} else {
			filePath = "internal/config/config_test.yaml"
		}

		cfg, err := LoadConfig[Config](filePath); 
    if err == nil {
      configInstance = cfg
      logger.Println("Configuration initialized successfully.")
    } else {
      logger.Fatalf("Failed to initialize config: %v", err)
    }
  }
}