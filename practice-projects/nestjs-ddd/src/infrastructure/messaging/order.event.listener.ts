import { Injectable, Logger } from '@nestjs/common';
import { OnEvent } from '@nestjs/event-emitter';
import {
  OrderCancelledEvent,
  OrderCreatedEvent,
  OrderPaidEvent,
} from '../../domain/order/events/order.events';

/**
 * 订单事件监听器示例
 *
 * 业务上的副作用（发送邮件、短信、触发下游服务等）放在这里。
 * 这样应用服务只关心核心用例，副作用通过事件解耦。
 */
@Injectable()
export class OrderEventListener {
  private readonly logger = new Logger(OrderEventListener.name);

  @OnEvent(OrderCreatedEvent.EVENT_NAME)
  handleOrderCreated(event: OrderCreatedEvent): void {
    this.logger.log(
      `[订单事件] 订单创建 orderId=${event.orderId}, orderNo=${event.orderNo}, amount=${event.totalAmount}`,
    );
  }

  @OnEvent(OrderPaidEvent.EVENT_NAME)
  handleOrderPaid(event: OrderPaidEvent): void {
    this.logger.log(`[订单事件] 订单支付 orderId=${event.orderId}, orderNo=${event.orderNo}`);
  }

  @OnEvent(OrderCancelledEvent.EVENT_NAME)
  handleOrderCancelled(event: OrderCancelledEvent): void {
    this.logger.log(`[订单事件] 订单取消 orderId=${event.orderId}, orderNo=${event.orderNo}`);
  }
}
