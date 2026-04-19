import { BaseDomainEvent } from '../../shared/events/domain-event';
import { OrderStatus } from '../model/order.entity';

export class OrderCreatedEvent extends BaseDomainEvent {
  static readonly EVENT_NAME = 'order.created';
  readonly eventName = OrderCreatedEvent.EVENT_NAME;

  constructor(
    public readonly orderId: number,
    public readonly orderNo: string,
    public readonly userId: number,
    public readonly totalAmount: number,
    public readonly status: OrderStatus,
  ) {
    super();
  }
}

export class OrderPaidEvent extends BaseDomainEvent {
  static readonly EVENT_NAME = 'order.paid';
  readonly eventName = OrderPaidEvent.EVENT_NAME;

  constructor(
    public readonly orderId: number,
    public readonly orderNo: string,
    public readonly userId: number,
    public readonly totalAmount: number,
  ) {
    super();
  }
}

export class OrderCancelledEvent extends BaseDomainEvent {
  static readonly EVENT_NAME = 'order.cancelled';
  readonly eventName = OrderCancelledEvent.EVENT_NAME;

  constructor(
    public readonly orderId: number,
    public readonly orderNo: string,
    public readonly userId: number,
  ) {
    super();
  }
}
