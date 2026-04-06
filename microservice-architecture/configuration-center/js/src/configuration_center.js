export class ConfigCenter {
  constructor() {
    this.store = new Map()
  }

  put(config) {
    this.store.set(`${config.serviceName}@${config.environment}`, config)
  }

  get(serviceName, environment) {
    return this.store.get(`${serviceName}@${environment}`) || null
  }
}

export class ConfigClient {
  constructor(center, serviceName, environment) {
    this.center = center
    this.serviceName = serviceName
    this.environment = environment
    this.currentConfig = null
  }

  load() {
    this.currentConfig = this.center.get(this.serviceName, this.environment)
    return this.currentConfig
  }

  refresh() {
    return this.load()
  }

  current() {
    return this.currentConfig
  }
}
