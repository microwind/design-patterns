import {
  Column,
  CreateDateColumn,
  Entity,
  Index,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from 'typeorm';
import { OrderStatus } from '../../../domain/order/model/order.entity';

/**
 * 订单 ORM 实体
 * 表结构与 gin-ddd/docs/init.sql 对齐：
 *   id BIGINT PK, order_no UNIQUE, user_id, total_amount DECIMAL(10,2),
 *   status, created_at, updated_at。
 */
@Entity({ name: 'orders' })
export class OrderOrmEntity {
  @PrimaryGeneratedColumn({ name: 'id', type: 'bigint' })
  id!: number;

  @Index({ unique: true })
  @Column({ name: 'order_no', length: 50 })
  orderNo!: string;

  @Index()
  @Column({ name: 'user_id', type: 'bigint' })
  userId!: number;

  @Column({ name: 'total_amount', type: 'decimal', precision: 10, scale: 2 })
  totalAmount!: number;

  @Index()
  @Column({ name: 'status', type: 'varchar', length: 20, default: OrderStatus.PENDING })
  status!: OrderStatus;

  @CreateDateColumn({ name: 'created_at' })
  createdAt!: Date;

  @UpdateDateColumn({ name: 'updated_at' })
  updatedAt!: Date;
}
