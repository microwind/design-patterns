import { Inject, Injectable, Logger, NotFoundException } from '@nestjs/common';
import {
  OrderCancelledEvent,
  OrderCreatedEvent,
  OrderPaidEvent,
} from '../../domain/order/events/order.events';
import { Order } from '../../domain/order/model/order.entity';
import {
  ORDER_REPOSITORY,
  OrderRepository,
} from '../../domain/order/repository/order.repository';
import {
  EVENT_PUBLISHER,
  EventPublisher,
} from '../../domain/shared/events/event-publisher';
import {
  USER_REPOSITORY,
  UserRepository,
} from '../../domain/user/repository/user.repository';
import { CreateOrderCommand } from './dto/order.commands';
import { OrderDto } from './dto/order.dto';

@Injectable()
export class OrderApplicationService {
  private readonly logger = new Logger(OrderApplicationService.name);

  constructor(
    @Inject(ORDER_REPOSITORY)
    private readonly orderRepository: OrderRepository,
    @Inject(USER_REPOSITORY)
    private readonly userRepository: UserRepository,
    @Inject(EVENT_PUBLISHER)
    private readonly eventPublisher: EventPublisher,
  ) {}

  async createOrder(command: CreateOrderCommand): Promise<OrderDto> {
    this.logger.log(`创建订单: userId=${command.userId}, amount=${command.totalAmount}`);

    const user = await this.userRepository.findById(command.userId);
    if (!user) {
      throw new NotFoundException(`用户不存在: id=${command.userId}`);
    }

    const orderNo = this.generateOrderNo();
    const order = Order.create({
      orderNo,
      userId: command.userId,
      totalAmount: command.totalAmount,
    });

    const saved = await this.orderRepository.create(order);
    this.logger.log(`订单创建成功: id=${saved.id}, orderNo=${saved.orderNo}`);

    await this.eventPublisher.publish(
      new OrderCreatedEvent(
        saved.id!,
        saved.orderNo,
        saved.userId,
        saved.totalAmount,
        saved.status,
      ),
    );

    return OrderDto.fromEntity(saved);
  }

  async getOrderById(id: number): Promise<OrderDto> {
    const order = await this.orderRepository.findById(id);
    if (!order) {
      throw new NotFoundException(`订单不存在: id=${id}`);
    }
    return OrderDto.fromEntity(order);
  }

  async getOrderByOrderNo(orderNo: string): Promise<OrderDto> {
    const order = await this.orderRepository.findByOrderNo(orderNo);
    if (!order) {
      throw new NotFoundException(`订单不存在: orderNo=${orderNo}`);
    }
    return OrderDto.fromEntity(order);
  }

  async getUserOrders(userId: number): Promise<OrderDto[]> {
    const orders = await this.orderRepository.findByUserId(userId);
    return OrderDto.fromEntities(orders);
  }

  async getAllOrders(): Promise<OrderDto[]> {
    const orders = await this.orderRepository.findAll();
    return OrderDto.fromEntities(orders);
  }

  async payOrder(id: number): Promise<OrderDto> {
    const order = await this.findOrThrow(id);
    order.pay();
    const updated = await this.orderRepository.update(order);

    await this.eventPublisher.publish(
      new OrderPaidEvent(updated.id!, updated.orderNo, updated.userId, updated.totalAmount),
    );
    return OrderDto.fromEntity(updated);
  }

  async shipOrder(id: number): Promise<OrderDto> {
    const order = await this.findOrThrow(id);
    order.ship();
    const updated = await this.orderRepository.update(order);
    return OrderDto.fromEntity(updated);
  }

  async deliverOrder(id: number): Promise<OrderDto> {
    const order = await this.findOrThrow(id);
    order.deliver();
    const updated = await this.orderRepository.update(order);
    return OrderDto.fromEntity(updated);
  }

  async cancelOrder(id: number): Promise<OrderDto> {
    const order = await this.findOrThrow(id);
    order.cancel();
    const updated = await this.orderRepository.update(order);

    await this.eventPublisher.publish(
      new OrderCancelledEvent(updated.id!, updated.orderNo, updated.userId),
    );
    return OrderDto.fromEntity(updated);
  }

  async refundOrder(id: number): Promise<OrderDto> {
    const order = await this.findOrThrow(id);
    order.refund();
    const updated = await this.orderRepository.update(order);
    return OrderDto.fromEntity(updated);
  }

  private async findOrThrow(id: number): Promise<Order> {
    const order = await this.orderRepository.findById(id);
    if (!order) {
      throw new NotFoundException(`订单不存在: id=${id}`);
    }
    return order;
  }

  private generateOrderNo(): string {
    const ts = Date.now();
    const rand = Math.floor(Math.random() * 1000)
      .toString()
      .padStart(3, '0');
    return `ORD${ts}${rand}`;
  }
}
