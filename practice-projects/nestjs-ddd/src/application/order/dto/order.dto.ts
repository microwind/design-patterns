import { Order, OrderStatus } from '../../../domain/order/model/order.entity';

export class OrderDto {
  id!: number;
  orderNo!: string;
  userId!: number;
  totalAmount!: number;
  status!: OrderStatus;
  createdAt!: Date;
  updatedAt!: Date;

  static fromEntity(order: Order): OrderDto {
    const dto = new OrderDto();
    dto.id = order.id!;
    dto.orderNo = order.orderNo;
    dto.userId = order.userId;
    dto.totalAmount = order.totalAmount;
    dto.status = order.status;
    dto.createdAt = order.createdAt;
    dto.updatedAt = order.updatedAt;
    return dto;
  }

  static fromEntities(orders: Order[]): OrderDto[] {
    return orders.map((o) => OrderDto.fromEntity(o));
  }
}
