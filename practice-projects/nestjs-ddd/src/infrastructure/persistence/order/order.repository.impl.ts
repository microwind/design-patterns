import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Order, OrderStatus } from '../../../domain/order/model/order.entity';
import { OrderRepository } from '../../../domain/order/repository/order.repository';
import { ORDER_DATASOURCE } from '../../config/typeorm-config.service';
import { OrderOrmEntity } from './order.orm-entity';

@Injectable()
export class OrderRepositoryImpl implements OrderRepository {
  constructor(
    @InjectRepository(OrderOrmEntity, ORDER_DATASOURCE)
    private readonly repo: Repository<OrderOrmEntity>,
  ) {}

  async create(order: Order): Promise<Order> {
    const saved = await this.repo.save(this.toOrm(order));
    return this.toDomain(saved);
  }

  async update(order: Order): Promise<Order> {
    if (!order.id) {
      throw new Error('更新订单时 id 不能为空');
    }
    const saved = await this.repo.save(this.toOrm(order));
    return this.toDomain(saved);
  }

  async findById(id: number): Promise<Order | null> {
    const orm = await this.repo.findOne({ where: { id } });
    return orm ? this.toDomain(orm) : null;
  }

  async findByOrderNo(orderNo: string): Promise<Order | null> {
    const orm = await this.repo.findOne({ where: { orderNo } });
    return orm ? this.toDomain(orm) : null;
  }

  async findByUserId(userId: number): Promise<Order[]> {
    const list = await this.repo.find({ where: { userId }, order: { id: 'DESC' } });
    return list.map((o) => this.toDomain(o));
  }

  async findAll(): Promise<Order[]> {
    const list = await this.repo.find({ order: { id: 'DESC' } });
    return list.map((o) => this.toDomain(o));
  }

  private toDomain(orm: OrderOrmEntity): Order {
    return Order.restore({
      id: Number(orm.id),
      orderNo: orm.orderNo,
      userId: Number(orm.userId),
      totalAmount: Number(orm.totalAmount),
      status: orm.status as OrderStatus,
      createdAt: orm.createdAt,
      updatedAt: orm.updatedAt,
    });
  }

  private toOrm(order: Order): OrderOrmEntity {
    const orm = new OrderOrmEntity();
    if (order.id) {
      orm.id = order.id;
    }
    orm.orderNo = order.orderNo;
    orm.userId = order.userId;
    orm.totalAmount = order.totalAmount;
    orm.status = order.status;
    orm.createdAt = order.createdAt;
    orm.updatedAt = order.updatedAt;
    return orm;
  }
}
