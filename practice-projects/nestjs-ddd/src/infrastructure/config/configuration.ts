import * as fs from 'fs';
import * as path from 'path';
import * as yaml from 'js-yaml';

export type DbType = 'mysql' | 'postgres';

export interface DatabaseConnectionConfig {
  type: DbType;
  host: string;
  port: number;
  username: string;
  password: string;
  database: string;
  synchronize: boolean;
  logging: boolean;
}

export interface AppConfig {
  server: {
    host: string;
    port: number;
    mode: 'debug' | 'release' | 'test';
    apiPrefix: string;
  };
  database: {
    user: DatabaseConnectionConfig;
    order: DatabaseConnectionConfig;
  };
  logger: {
    level: string;
    format: string;
  };
  event: {
    enabled: boolean;
  };
}

const CONFIG_PATH = path.resolve(
  process.cwd(),
  process.env.CONFIG_FILE || 'config/config.yaml',
);

/**
 * 读取 YAML 配置文件，风格与 gin-ddd 的 config.yaml 对齐。
 * 注意：返回的对象结构是 camelCase，但 YAML 中依旧允许 snake_case，由本函数做一次归一化。
 */
export function loadYamlConfig(): AppConfig {
  if (!fs.existsSync(CONFIG_PATH)) {
    throw new Error(`配置文件不存在: ${CONFIG_PATH}`);
  }
  const raw = yaml.load(fs.readFileSync(CONFIG_PATH, 'utf8')) as any;

  return {
    server: {
      host: raw.server?.host ?? '0.0.0.0',
      port: Number(raw.server?.port ?? 8080),
      mode: (raw.server?.mode ?? 'debug') as AppConfig['server']['mode'],
      apiPrefix: raw.server?.api_prefix ?? 'api',
    },
    database: {
      user: toDbConfig(raw.database?.user),
      order: toDbConfig(raw.database?.order),
    },
    logger: {
      level: raw.logger?.level ?? 'info',
      format: raw.logger?.format ?? 'text',
    },
    event: {
      enabled: raw.event?.enabled ?? true,
    },
  };
}

function toDbConfig(node: any): DatabaseConnectionConfig {
  if (!node) {
    throw new Error('配置文件中缺少 database 节');
  }
  return {
    type: (node.type ?? 'mysql') as DbType,
    host: node.host ?? 'localhost',
    port: Number(node.port),
    username: node.username ?? '',
    password: node.password ?? '',
    database: node.database ?? '',
    synchronize: Boolean(node.synchronize ?? true),
    logging: Boolean(node.logging ?? false),
  };
}
