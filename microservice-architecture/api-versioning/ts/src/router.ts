export type Request = {
  path: string;
  headers: Record<string, string>;
};

export type Response = {
  statusCode: number;
  version: string;
  body: string;
};

export class VersionedRouter {
  private handlers: Record<string, () => string> = {};

  constructor(private readonly defaultVersion: string) {}

  register(version: string, handler: () => string): void {
    this.handlers[this.normalize(version)] = handler;
  }

  handle(request: Request): Response {
    const version = this.resolveVersion(request);
    const handler = this.handlers[version];
    if (!handler) {
      return { statusCode: 400, version, body: "unsupported api version" };
    }
    return { statusCode: 200, version, body: handler() };
  }

  resolveVersion(request: Request): string {
    const path = request.path.toLowerCase();
    if (path.includes("/v2/")) {
      return "v2";
    }
    if (path.includes("/v1/")) {
      return "v1";
    }
    const headerVersion = this.normalize(request.headers["X-API-Version"] ?? "");
    if (headerVersion) {
      return headerVersion;
    }
    return this.normalize(this.defaultVersion);
  }

  private normalize(version: string): string {
    const normalized = version.trim().toLowerCase();
    if (!normalized) {
      return "";
    }
    return normalized.startsWith("v") ? normalized : `v${normalized}`;
  }
}

export function productHandlerV1(): string {
  return '{"id":"P100","name":"Mechanical Keyboard"}';
}

export function productHandlerV2(): string {
  return '{"id":"P100","name":"Mechanical Keyboard","inventoryStatus":"IN_STOCK"}';
}
