export class ServiceRegistry {
  constructor() {
    this.services = new Map()
  }

  register(serviceName, instance) {
    if (!this.services.has(serviceName)) {
      this.services.set(serviceName, new Map())
    }
    this.services.get(serviceName).set(instance.instanceId, instance)
  }

  deregister(serviceName, instanceId) {
    const instances = this.services.get(serviceName)
    if (!instances || !instances.has(instanceId)) {
      return false
    }
    instances.delete(instanceId)
    return true
  }

  instances(serviceName) {
    const instances = Array.from((this.services.get(serviceName) || new Map()).values())
    return instances.sort((a, b) => a.instanceId.localeCompare(b.instanceId))
  }
}

export class RoundRobinDiscoverer {
  constructor(registry) {
    this.registry = registry
    this.offsets = new Map()
  }

  next(serviceName) {
    const instances = this.registry.instances(serviceName)
    if (instances.length === 0) {
      return null
    }
    const index = (this.offsets.get(serviceName) || 0) % instances.length
    this.offsets.set(serviceName, (this.offsets.get(serviceName) || 0) + 1)
    return instances[index]
  }
}
