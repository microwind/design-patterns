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
