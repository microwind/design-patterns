export type Backend = {
  backendId: string;
  weight?: number;
  activeConnections?: number;
};

export class RoundRobinBalancer {
  private nextIndex = 0;

  constructor(private readonly backends: Backend[]) {}

  next(): Backend {
    const backend = this.backends[this.nextIndex % this.backends.length];
    this.nextIndex++;
    return backend;
  }
}

export class WeightedRoundRobinBalancer {
  private sequence: Backend[] = [];
  private nextIndex = 0;

  constructor(backends: Backend[]) {
    for (const backend of backends) {
      const weight = backend.weight && backend.weight > 0 ? backend.weight : 1;
      for (let i = 0; i < weight; i++) {
        this.sequence.push(backend);
      }
    }
  }

  next(): Backend {
    const backend = this.sequence[this.nextIndex % this.sequence.length];
    this.nextIndex++;
    return backend;
  }
}

export class LeastConnectionsBalancer {
  private backends: Record<string, Required<Backend>> = {};

  constructor(backends: Backend[]) {
    for (const backend of backends) {
      this.backends[backend.backendId] = {
        backendId: backend.backendId,
        weight: backend.weight ?? 1,
        activeConnections: backend.activeConnections ?? 0
      };
    }
  }

  acquire(): Required<Backend> {
    const backend = Object.values(this.backends).reduce((best, current) =>
      current.activeConnections < best.activeConnections ? current : best
    );
    backend.activeConnections++;
    return backend;
  }

  release(backendId: string): void {
    const backend = this.backends[backendId];
    if (backend && backend.activeConnections > 0) {
      backend.activeConnections--;
    }
  }
}
