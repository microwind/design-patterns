// Package src 实现了服务发现模式（Service Discovery Pattern）的核心逻辑。
//
// 【设计模式】
//   - 注册表模式（Registry Pattern）：ServiceRegistry 维护服务名到实例列表的映射。
//   - 策略模式（Strategy Pattern）：RoundRobinDiscoverer 封装轮询选择策略。
//
// 【架构思想】
//   服务发现解决微服务架构中"调用方如何找到被调服务"的问题。
//
// 【开源对比】
//   - Consul：HashiCorp 的 CP 注册中心，Go 语言编写
//   - etcd：CoreOS 的分布式 KV 存储，常用于 Kubernetes 服务发现
//   本示例用内存 map 简化，省略了心跳、健康检查和集群同步。
package src

import "sort"

// ServiceInstance 表示一个服务实例。
type ServiceInstance struct {
	ID      string // 实例唯一标识
	Address string // 实例网络地址
}

// ServiceRegistry 是服务注册中心。
// 【设计模式】注册表模式：维护服务名到实例列表的全局映射。
type ServiceRegistry struct {
	services map[string]map[string]ServiceInstance // 服务名 -> (实例ID -> 实例)
}

// NewServiceRegistry 创建注册中心。
func NewServiceRegistry() *ServiceRegistry {
	return &ServiceRegistry{services: map[string]map[string]ServiceInstance{}}
}

// Register 注册服务实例。同一 ID 重复注册会覆盖旧实例（幂等）。
func (r *ServiceRegistry) Register(service string, instance ServiceInstance) {
	if r.services[service] == nil {
		r.services[service] = map[string]ServiceInstance{}
	}
	r.services[service][instance.ID] = instance
}

// Deregister 摘除服务实例。返回是否摘除成功。
func (r *ServiceRegistry) Deregister(service string, instanceID string) bool {
	if r.services[service] == nil {
		return false
	}
	if _, exists := r.services[service][instanceID]; !exists {
		return false
	}
	delete(r.services[service], instanceID)
	return true
}

// Instances 获取指定服务的所有可用实例（按 ID 排序，保证轮询稳定）。
func (r *ServiceRegistry) Instances(service string) []ServiceInstance {
	raw := r.services[service]
	if len(raw) == 0 {
		return []ServiceInstance{}
	}

	instances := make([]ServiceInstance, 0, len(raw))
	for _, instance := range raw {
		instances = append(instances, instance)
	}

	// 排序保证轮询结果的确定性
	sort.Slice(instances, func(i int, j int) bool {
		return instances[i].ID < instances[j].ID
	})

	return instances
}

// RoundRobinDiscoverer 是轮询服务发现客户端。
// 【设计模式】策略模式：封装轮询选择策略，实际工程中还支持随机、加权等策略。
type RoundRobinDiscoverer struct {
	registry *ServiceRegistry // 关联的注册中心
	offsets  map[string]int   // 每个服务的轮询偏移量
}

// NewRoundRobinDiscoverer 创建轮询发现客户端。
func NewRoundRobinDiscoverer(registry *ServiceRegistry) *RoundRobinDiscoverer {
	return &RoundRobinDiscoverer{
		registry: registry,
		offsets:  map[string]int{},
	}
}

// Next 获取下一个可用实例（轮询策略）。
// 返回实例和是否找到的标志。
func (d *RoundRobinDiscoverer) Next(service string) (ServiceInstance, bool) {
	instances := d.registry.Instances(service)
	if len(instances) == 0 {
		return ServiceInstance{}, false
	}

	// 取模实现轮询
	index := d.offsets[service] % len(instances)
	d.offsets[service]++
	return instances[index], true
}
