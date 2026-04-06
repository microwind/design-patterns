package src

import "fmt"

type ServiceConfig struct {
	ServiceName       string
	Environment       string
	Version           int
	DbHost            string
	TimeoutMs         int
	FeatureOrderAudit bool
}

type ConfigCenter struct {
	store map[string]ServiceConfig
}

func NewConfigCenter() *ConfigCenter {
	return &ConfigCenter{store: map[string]ServiceConfig{}}
}

func (c *ConfigCenter) Put(config ServiceConfig) {
	c.store[key(config.ServiceName, config.Environment)] = config
}

func (c *ConfigCenter) Get(serviceName string, environment string) (ServiceConfig, bool) {
	config, ok := c.store[key(serviceName, environment)]
	return config, ok
}

type ConfigClient struct {
	center      *ConfigCenter
	serviceName string
	environment string
	current     ServiceConfig
	loaded      bool
}

func NewConfigClient(center *ConfigCenter, serviceName string, environment string) *ConfigClient {
	return &ConfigClient{center: center, serviceName: serviceName, environment: environment}
}

func (c *ConfigClient) Load() (ServiceConfig, bool) {
	config, ok := c.center.Get(c.serviceName, c.environment)
	if ok {
		c.current = config
		c.loaded = true
	}
	return config, ok
}

func (c *ConfigClient) Refresh() (ServiceConfig, bool) {
	return c.Load()
}

func (c *ConfigClient) Current() (ServiceConfig, bool) {
	return c.current, c.loaded
}

func key(serviceName string, environment string) string {
	return fmt.Sprintf("%s@%s", serviceName, environment)
}
