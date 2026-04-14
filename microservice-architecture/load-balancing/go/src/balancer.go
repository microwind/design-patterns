// Package src 实现了负载均衡模式（Load Balancing Pattern）的核心逻辑。
//
// 本模块演示三种经典负载均衡算法：轮询、加权轮询和最少连接。
//
// 【设计模式】
//   - 策略模式（Strategy Pattern）：三种负载均衡算法是可互换的策略，
//     各自实现不同的分配逻辑，调用方根据场景选择。
//   - 迭代器模式（Iterator Pattern）：RoundRobin 和 WeightedRoundRobin
//     的 Next() 方法通过取模实现循环迭代。
//
// 【架构思想】
//   负载均衡将流量分散到多个后端实例，避免单点过载。
//
// 【开源对比】
//   - gRPC-Go：内置 round-robin、pick-first 等负载均衡策略
//   - Traefik（Go 编写）：支持 WRR、mirror 等多种策略
//   本示例省略了健康检查和动态权重调整等工程细节，聚焦于算法核心。
package src

type Backend struct {
	ID                string
	Weight            int
	ActiveConnections int
}

type RoundRobinBalancer struct {
	backends []Backend
	next     int
}

func NewRoundRobinBalancer(backends []Backend) *RoundRobinBalancer {
	return &RoundRobinBalancer{backends: cloneBackends(backends)}
}

func (b *RoundRobinBalancer) Next() Backend {
	index := b.next % len(b.backends)
	b.next++
	return b.backends[index]
}

type WeightedRoundRobinBalancer struct {
	sequence []Backend
	next     int
}

func NewWeightedRoundRobinBalancer(backends []Backend) *WeightedRoundRobinBalancer {
	sequence := make([]Backend, 0)
	for _, backend := range backends {
		weight := backend.Weight
		if weight <= 0 {
			weight = 1
		}
		for i := 0; i < weight; i++ {
			sequence = append(sequence, backend)
		}
	}
	return &WeightedRoundRobinBalancer{sequence: sequence}
}

func (b *WeightedRoundRobinBalancer) Next() Backend {
	index := b.next % len(b.sequence)
	b.next++
	return b.sequence[index]
}

type LeastConnectionsBalancer struct {
	backends map[string]*Backend
}

func NewLeastConnectionsBalancer(backends []Backend) *LeastConnectionsBalancer {
	indexed := map[string]*Backend{}
	for _, backend := range backends {
		copied := backend
		indexed[backend.ID] = &copied
	}
	return &LeastConnectionsBalancer{backends: indexed}
}

func (b *LeastConnectionsBalancer) Acquire() Backend {
	var chosen *Backend
	for _, backend := range b.backends {
		if chosen == nil || backend.ActiveConnections < chosen.ActiveConnections {
			chosen = backend
		}
	}

	chosen.ActiveConnections++
	return *chosen
}

func (b *LeastConnectionsBalancer) Release(backendID string) {
	backend := b.backends[backendID]
	if backend != nil && backend.ActiveConnections > 0 {
		backend.ActiveConnections--
	}
}

func cloneBackends(backends []Backend) []Backend {
	cloned := make([]Backend, len(backends))
	copy(cloned, backends)
	return cloned
}
