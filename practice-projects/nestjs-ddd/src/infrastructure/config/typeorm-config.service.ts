import { TypeOrmModuleOptions } from '@nestjs/typeorm';
import { DatabaseConnectionConfig } from './configuration';
import { OrderOrmEntity } from '../persistence/order/order.orm-entity';
import { UserOrmEntity } from '../persistence/user/user.orm-entity';

/**
 * 两个命名的数据源，与 gin-ddd 保持一致：
 *  - user_db: 默认 MySQL（库 frog）承载用户数据
 *  - order_db: 默认 PostgreSQL（库 seed）承载订单数据
 *
 * 各模块通过 TypeOrmModule.forRootAsync({ name }) + TypeOrmModule.forFeature([...], name)
 * 引用对应连接，保证业务边界清晰、可独立扩展。
 */
export const USER_DATASOURCE = 'user_db';
export const ORDER_DATASOURCE = 'order_db';

export function buildUserDataSource(cfg: DatabaseConnectionConfig): TypeOrmModuleOptions {
  return buildDataSource(USER_DATASOURCE, cfg, [UserOrmEntity]);
}

export function buildOrderDataSource(cfg: DatabaseConnectionConfig): TypeOrmModuleOptions {
  return buildDataSource(ORDER_DATASOURCE, cfg, [OrderOrmEntity]);
}

function buildDataSource(
  name: string,
  cfg: DatabaseConnectionConfig,
  entities: Function[],
): TypeOrmModuleOptions {
  const base = {
    name,
    host: cfg.host,
    port: cfg.port,
    username: cfg.username,
    password: cfg.password,
    database: cfg.database,
    entities,
    synchronize: cfg.synchronize,
    logging: cfg.logging,
  };

  if (cfg.type === 'mysql') {
    return { ...base, type: 'mysql', charset: 'utf8mb4_unicode_ci' } as TypeOrmModuleOptions;
  }
  if (cfg.type === 'postgres') {
    return { ...base, type: 'postgres' } as TypeOrmModuleOptions;
  }
  throw new Error(`不支持的数据库类型: ${cfg.type}`);
}
