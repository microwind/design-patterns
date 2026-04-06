export class APIGateway {
  constructor() {
    this.routes = new Map()
    this.middlewares = []
  }

  use(middleware) {
    this.middlewares.push(middleware)
  }

  register(prefix, handler) {
    this.routes.set(prefix, handler)
  }

  handle(request) {
    for (const middleware of this.middlewares) {
      const response = middleware(request)
      if (response !== null) {
        return response
      }
    }

    const handler = this.match(request.path)
    if (!handler) {
      return { statusCode: 404, body: 'gateway: route not found', headers: {} }
    }

    const response = handler(request)
    response.headers['X-Correlation-ID'] =
      response.headers['X-Correlation-ID'] ||
      request.headers['X-Correlation-ID'] ||
      'gw-generated-correlation-id'
    return response
  }

  match(path) {
    let matchedPrefix = ''
    let matchedHandler = null

    for (const [prefix, handler] of this.routes.entries()) {
      if (path.startsWith(prefix) && prefix.length > matchedPrefix.length) {
        matchedPrefix = prefix
        matchedHandler = handler
      }
    }

    return matchedHandler
  }
}

export function requireUserHeader(prefix, headerName) {
  return (request) => {
    if (!request.path.startsWith(prefix)) {
      return null
    }
    if (!request.headers[headerName]) {
      return { statusCode: 401, body: 'gateway: unauthorized', headers: {} }
    }
    return null
  }
}

export function orderServiceHandler() {
  return (request) => ({
    statusCode: 200,
    body: `order-service handled ${request.path}`,
    headers: { 'X-Upstream-Service': 'order-service' }
  })
}

export function inventoryServiceHandler() {
  return (request) => ({
    statusCode: 200,
    body: `inventory-service handled ${request.path}`,
    headers: { 'X-Upstream-Service': 'inventory-service' }
  })
}
