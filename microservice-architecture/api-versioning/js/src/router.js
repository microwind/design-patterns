export class VersionedRouter {
  constructor(defaultVersion) {
    this.defaultVersion = this.normalize(defaultVersion)
    this.handlers = new Map()
  }

  register(version, handler) {
    this.handlers.set(this.normalize(version), handler)
  }

  handle(request) {
    const version = this.resolveVersion(request)
    const handler = this.handlers.get(version)
    if (!handler) {
      return { statusCode: 400, version, body: 'unsupported api version' }
    }
    return { statusCode: 200, version, body: handler() }
  }

  resolveVersion(request) {
    const path = request.path.toLowerCase()
    if (path.includes('/v2/')) {
      return 'v2'
    }
    if (path.includes('/v1/')) {
      return 'v1'
    }
    const headerVersion = this.normalize(request.headers['X-API-Version'] || '')
    if (headerVersion) {
      return headerVersion
    }
    return this.defaultVersion
  }

  normalize(version) {
    const normalized = version.trim().toLowerCase()
    if (!normalized) {
      return ''
    }
    return normalized.startsWith('v') ? normalized : `v${normalized}`
  }
}

export function productHandlerV1() {
  return '{"id":"P100","name":"Mechanical Keyboard"}'
}

export function productHandlerV2() {
  return '{"id":"P100","name":"Mechanical Keyboard","inventoryStatus":"IN_STOCK"}'
}
