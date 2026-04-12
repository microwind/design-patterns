// Package src 实现了配置中心模式（Configuration Center Pattern）的核心逻辑。
//
// 【设计模式】
//   - 观察者模式：实际工程中配置变更会推送通知，本示例简化为客户端主动 refresh。
//   - 单例模式：ConfigCenter 通常全局唯一。
//   - 代理模式：ConfigClient 代理 ConfigCenter 访问并缓存配置。
//
// 【架构思想】
//   配置中心将所有服务配置集中存储，按"服务名+环境"维度管理。
//
// 【开源对比】
//   - etcd：Go 编写的分布式 KV 存储，支持 watch 机制
//   - Consul KV：HashiCorp 的配置存储，支持长轮询
//   - Nacos：支持配置管理和服务发现
//   本示例用内存 map 简化，省略了持久化和推送。
package src

import "fmt"

// ServiceConfig 表示一个服务的配置（值对象）。
type ServiceConfig struct {
	ServiceName       string // 服务名称
	Environment       string // 环境标识（dev / staging / prod）
	Version           int    // 配置版本号
	DbHost            string // 数据库地址
	TimeoutMs         int    // 超时时间（毫秒）
	FeatureOrderAudit bool   // 订单审计功能开关
}

// ConfigCenter 是配置中心服务端。
// 【设计模式】注册表模式：按 "serviceName@environment" 键存储配置。
type ConfigCenter struct {
	store map[string]ServiceConfig // 配置存储
}

// NewConfigCenter 创建配置中心。
func NewConfigCenter() *ConfigCenter {
	return &ConfigCenter{store: map[string]ServiceConfig{}}
}

// Put 发布配置。同一 key 重复发布会覆盖（支持配置更新）。
func (c *ConfigCenter) Put(config ServiceConfig) {
	c.store[key(config.ServiceName, config.Environment)] = config
}

// Get 获取指定服务和环境的配置。
func (c *ConfigCenter) Get(serviceName string, environment string) (ServiceConfig, bool) {
	config, ok := c.store[key(serviceName, environment)]
	return config, ok
}

// ConfigClient 是配置客户端。
// 【设计模式】代理模式：代理 ConfigCenter 访问，本地缓存当前配置快照。
type ConfigClient struct {
	center      *ConfigCenter  // 关联的配置中心
	serviceName string         // 绑定的服务名
	environment string         // 绑定的环境
	current     ServiceConfig  // 当前缓存的配置快照
	loaded      bool           // 是否已加载过
}

// NewConfigClient 创建配置客户端，绑定特定服务和环境。
func NewConfigClient(center *ConfigCenter, serviceName string, environment string) *ConfigClient {
	return &ConfigClient{center: center, serviceName: serviceName, environment: environment}
}

// Load 首次加载配置（从配置中心拉取并缓存）。
func (c *ConfigClient) Load() (ServiceConfig, bool) {
	config, ok := c.center.Get(c.serviceName, c.environment)
	if ok {
		c.current = config
		c.loaded = true
	}
	return config, ok
}

// Refresh 刷新配置（从配置中心重新拉取）。
func (c *ConfigClient) Refresh() (ServiceConfig, bool) {
	return c.Load()
}

// Current 获取当前缓存的配置快照。
func (c *ConfigClient) Current() (ServiceConfig, bool) {
	return c.current, c.loaded
}

// key 生成配置存储键。
func key(serviceName string, environment string) string {
	return fmt.Sprintf("%s@%s", serviceName, environment)
}
