export type Request = {
  method: string;
  path: string;
  headers: Record<string, string>;
};

export type Response = {
  statusCode: number;
  body: string;
  headers: Record<string, string>;
};

export type Handler = (request: Request) => Response;
export type Middleware = (request: Request) => Response | null;

export class APIGateway {
  private routes: Record<string, Handler> = {};
  private middlewares: Middleware[] = [];

  use(middleware: Middleware): void {
    this.middlewares.push(middleware);
  }

  register(prefix: string, handler: Handler): void {
    this.routes[prefix] = handler;
  }

  handle(request: Request): Response {
    for (const middleware of this.middlewares) {
      const response = middleware(request);
      if (response !== null) {
        return response;
      }
    }

    const handler = this.match(request.path);
    if (!handler) {
      return { statusCode: 404, body: "gateway: route not found", headers: {} };
    }

    const response = handler(request);
    if (!response.headers["X-Correlation-ID"]) {
      response.headers["X-Correlation-ID"] =
        request.headers["X-Correlation-ID"] ?? "gw-generated-correlation-id";
    }
    return response;
  }

  private match(path: string): Handler | null {
    let matchedPrefix = "";
    let matchedHandler: Handler | null = null;

    for (const [prefix, handler] of Object.entries(this.routes)) {
      if (path.startsWith(prefix) && prefix.length > matchedPrefix.length) {
        matchedPrefix = prefix;
        matchedHandler = handler;
      }
    }

    return matchedHandler;
  }
}

export function requireUserHeader(prefix: string, headerName: string): Middleware {
  return (request: Request): Response | null => {
    if (!request.path.startsWith(prefix)) {
      return null;
    }
    if (!request.headers[headerName]) {
      return { statusCode: 401, body: "gateway: unauthorized", headers: {} };
    }
    return null;
  };
}

export function orderServiceHandler(): Handler {
  return (request: Request): Response => ({
    statusCode: 200,
    body: `order-service handled ${request.path}`,
    headers: { "X-Upstream-Service": "order-service" }
  });
}

export function inventoryServiceHandler(): Handler {
  return (request: Request): Response => ({
    statusCode: 200,
    body: `inventory-service handled ${request.path}`,
    headers: { "X-Upstream-Service": "inventory-service" }
  });
}
