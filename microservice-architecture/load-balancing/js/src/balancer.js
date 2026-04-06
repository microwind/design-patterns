export class RoundRobinBalancer {
  constructor(backends) {
    this.backends = backends
    this.nextIndex = 0
  }

  next() {
    const backend = this.backends[this.nextIndex % this.backends.length]
    this.nextIndex++
    return backend
  }
}

export class WeightedRoundRobinBalancer {
  constructor(backends) {
    this.sequence = []
    this.nextIndex = 0

    for (const backend of backends) {
      const weight = backend.weight > 0 ? backend.weight : 1
      for (let i = 0; i < weight; i++) {
        this.sequence.push(backend)
      }
    }
  }

  next() {
    const backend = this.sequence[this.nextIndex % this.sequence.length]
    this.nextIndex++
    return backend
  }
}

export class LeastConnectionsBalancer {
  constructor(backends) {
    this.backends = new Map()
    for (const backend of backends) {
      this.backends.set(backend.backendId, {
        backendId: backend.backendId,
        weight: backend.weight || 1,
        activeConnections: backend.activeConnections || 0
      })
    }
  }

  acquire() {
    let chosen = null
    for (const backend of this.backends.values()) {
      if (!chosen || backend.activeConnections < chosen.activeConnections) {
        chosen = backend
      }
    }
    chosen.activeConnections++
    return chosen
  }

  release(backendId) {
    const backend = this.backends.get(backendId)
    if (backend && backend.activeConnections > 0) {
      backend.activeConnections--
    }
  }
}
