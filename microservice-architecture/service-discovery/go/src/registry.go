package src

import "sort"

type ServiceInstance struct {
	ID      string
	Address string
}

type ServiceRegistry struct {
	services map[string]map[string]ServiceInstance
}

func NewServiceRegistry() *ServiceRegistry {
	return &ServiceRegistry{services: map[string]map[string]ServiceInstance{}}
}

func (r *ServiceRegistry) Register(service string, instance ServiceInstance) {
	if r.services[service] == nil {
		r.services[service] = map[string]ServiceInstance{}
	}
	r.services[service][instance.ID] = instance
}

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

func (r *ServiceRegistry) Instances(service string) []ServiceInstance {
	raw := r.services[service]
	if len(raw) == 0 {
		return []ServiceInstance{}
	}

	instances := make([]ServiceInstance, 0, len(raw))
	for _, instance := range raw {
		instances = append(instances, instance)
	}

	sort.Slice(instances, func(i int, j int) bool {
		return instances[i].ID < instances[j].ID
	})

	return instances
}

type RoundRobinDiscoverer struct {
	registry *ServiceRegistry
	offsets  map[string]int
}

func NewRoundRobinDiscoverer(registry *ServiceRegistry) *RoundRobinDiscoverer {
	return &RoundRobinDiscoverer{
		registry: registry,
		offsets:  map[string]int{},
	}
}

func (d *RoundRobinDiscoverer) Next(service string) (ServiceInstance, bool) {
	instances := d.registry.Instances(service)
	if len(instances) == 0 {
		return ServiceInstance{}, false
	}

	index := d.offsets[service] % len(instances)
	d.offsets[service]++
	return instances[index], true
}
