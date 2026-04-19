import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { EventEmitterModule } from '@nestjs/event-emitter';
import { TypeOrmModule, TypeOrmModuleOptions } from '@nestjs/typeorm';
import { loadYamlConfig } from './infrastructure/config/configuration';
import {
  buildOrderDataSource,
  buildUserDataSource,
  ORDER_DATASOURCE,
  USER_DATASOURCE,
} from './infrastructure/config/typeorm-config.service';
import { AppController } from './interfaces/http/app.controller';
import { OrderModule } from './modules/order.module';
import { SharedModule } from './modules/shared.module';
import { UserModule } from './modules/user.module';

/**
 * AppModule 将所有模块、两条 TypeORM 数据源和全局基础设施组装起来。
 *
 * 数据源与 gin-ddd 一致：
 *   - USER_DATASOURCE：MySQL（默认库 frog）
 *   - ORDER_DATASOURCE：PostgreSQL（默认库 seed）
 */
@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      load: [() => loadYamlConfig()],
    }),
    EventEmitterModule.forRoot({
      wildcard: false,
      delimiter: '.',
      maxListeners: 20,
      verboseMemoryLeak: true,
    }),
    TypeOrmModule.forRootAsync({
      name: USER_DATASOURCE,
      inject: [ConfigService],
      useFactory: (config: ConfigService): TypeOrmModuleOptions =>
        buildUserDataSource(config.get('database.user')!),
    }),
    TypeOrmModule.forRootAsync({
      name: ORDER_DATASOURCE,
      inject: [ConfigService],
      useFactory: (config: ConfigService): TypeOrmModuleOptions =>
        buildOrderDataSource(config.get('database.order')!),
    }),
    SharedModule,
    UserModule,
    OrderModule,
  ],
  controllers: [AppController],
})
export class AppModule {}
