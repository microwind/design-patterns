import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { OrderApplicationService } from '../application/order/order-application.service';
import { ORDER_REPOSITORY } from '../domain/order/repository/order.repository';
import { ORDER_DATASOURCE } from '../infrastructure/config/typeorm-config.service';
import { OrderEventListener } from '../infrastructure/messaging/order.event.listener';
import { OrderOrmEntity } from '../infrastructure/persistence/order/order.orm-entity';
import { OrderRepositoryImpl } from '../infrastructure/persistence/order/order.repository.impl';
import { OrderController } from '../interfaces/http/order/order.controller';
import { UserOrderController } from '../interfaces/http/order/user-order.controller';
import { SharedModule } from './shared.module';
import { UserModule } from './user.module';

/**
 * 订单模块 - 使用 order_db 数据源（默认 PostgreSQL seed 库）
 *
 * 依赖 UserModule（通过 USER_REPOSITORY）用于订单创建时查询用户信息，
 * 事件总线由 SharedModule 全局提供。
 */
@Module({
  imports: [
    TypeOrmModule.forFeature([OrderOrmEntity], ORDER_DATASOURCE),
    UserModule,
    SharedModule,
  ],
  controllers: [OrderController, UserOrderController],
  providers: [
    OrderApplicationService,
    OrderEventListener,
    {
      provide: ORDER_REPOSITORY,
      useClass: OrderRepositoryImpl,
    },
  ],
  exports: [OrderApplicationService, ORDER_REPOSITORY],
})
export class OrderModule {}
