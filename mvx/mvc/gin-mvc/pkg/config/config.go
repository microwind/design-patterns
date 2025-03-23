package config

import (
  "fmt"

  "github.com/spf13/viper"
)

var Config *viper.Viper

// Init 初始化 viper 配置
func Init() error {
  Config = viper.New()
  Config.SetConfigName("config")   // 配置文件名，不包含扩展名
  Config.SetConfigType("yaml")     // 配置文件类型
  Config.AddConfigPath("./config") // 配置文件路径

  // 读取配置文件
  if err := Config.ReadInConfig(); err != nil {
    return fmt.Errorf("failed to read config: %w", err)
  }
  return nil
}

// GetViper 返回 viper 实例
func GetViper() *viper.Viper {
  return Config
}
