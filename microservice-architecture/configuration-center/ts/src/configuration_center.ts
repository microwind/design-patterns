export type ServiceConfig = {
  serviceName: string;
  environment: string;
  version: number;
  dbHost: string;
  timeoutMs: number;
  featureOrderAudit: boolean;
};

export class ConfigCenter {
  private store: Record<string, ServiceConfig> = {};

  put(config: ServiceConfig): void {
    this.store[`${config.serviceName}@${config.environment}`] = config;
  }

  get(serviceName: string, environment: string): ServiceConfig | null {
    return this.store[`${serviceName}@${environment}`] ?? null;
  }
}

export class ConfigClient {
  private currentConfig: ServiceConfig | null = null;

  constructor(
    private readonly center: ConfigCenter,
    private readonly serviceName: string,
    private readonly environment: string
  ) {}

  load(): ServiceConfig | null {
    this.currentConfig = this.center.get(this.serviceName, this.environment);
    return this.currentConfig;
  }

  refresh(): ServiceConfig | null {
    return this.load();
  }

  current(): ServiceConfig | null {
    return this.currentConfig;
  }
}
