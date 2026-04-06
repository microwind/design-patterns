export type ServiceInstance = {
  instanceId: string;
  address: string;
};

export class ServiceRegistry {
  private services: Record<string, Record<string, ServiceInstance>> = {};

  register(serviceName: string, instance: ServiceInstance): void {
    if (!this.services[serviceName]) {
      this.services[serviceName] = {};
    }
    this.services[serviceName][instance.instanceId] = instance;
  }

  deregister(serviceName: string, instanceId: string): boolean {
    const instances = this.services[serviceName];
    if (!instances || !instances[instanceId]) {
      return false;
    }
    delete instances[instanceId];
    return true;
  }

  instances(serviceName: string): ServiceInstance[] {
    return Object.values(this.services[serviceName] ?? {}).sort((a, b) =>
      a.instanceId.localeCompare(b.instanceId)
    );
  }
}

export class RoundRobinDiscoverer {
  private offsets: Record<string, number> = {};

  constructor(private readonly registry: ServiceRegistry) {}

  next(serviceName: string): ServiceInstance | null {
    const instances = this.registry.instances(serviceName);
    if (instances.length === 0) {
      return null;
    }
    const index = (this.offsets[serviceName] ?? 0) % instances.length;
    this.offsets[serviceName] = (this.offsets[serviceName] ?? 0) + 1;
    return instances[index];
  }
}
