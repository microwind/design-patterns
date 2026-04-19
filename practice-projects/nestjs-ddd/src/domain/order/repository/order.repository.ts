import { Order } from '../model/order.entity';

export const ORDER_REPOSITORY = Symbol('ORDER_REPOSITORY');

export interface OrderRepository {
  create(order: Order): Promise<Order>;
  update(order: Order): Promise<Order>;
  findById(id: number): Promise<Order | null>;
  findByOrderNo(orderNo: string): Promise<Order | null>;
  findByUserId(userId: number): Promise<Order[]>;
  findAll(): Promise<Order[]>;
}
