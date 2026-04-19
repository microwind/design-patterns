/** 订单状态枚举 */
export enum OrderStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED',
}

/**
 * 订单聚合根
 *
 * 订单状态机的转换规则完全封装在聚合根内部。
 * 外部无法直接修改状态，只能通过业务方法触发，保证业务一致性。
 */
export class Order {
  private _id?: number;
  private _orderNo: string;
  private _userId: number;
  private _totalAmount: number;
  private _status: OrderStatus;
  private _createdAt: Date;
  private _updatedAt: Date;

  private constructor(props: {
    id?: number;
    orderNo: string;
    userId: number;
    totalAmount: number;
    status: OrderStatus;
    createdAt: Date;
    updatedAt: Date;
  }) {
    this._id = props.id;
    this._orderNo = props.orderNo;
    this._userId = props.userId;
    this._totalAmount = props.totalAmount;
    this._status = props.status;
    this._createdAt = props.createdAt;
    this._updatedAt = props.updatedAt;
  }

  static create(params: { orderNo: string; userId: number; totalAmount: number }): Order {
    if (!params.orderNo) {
      throw new Error('订单号不能为空');
    }
    if (!params.userId || params.userId <= 0) {
      throw new Error('用户 ID 无效');
    }
    if (!params.totalAmount || params.totalAmount <= 0) {
      throw new Error('订单金额必须大于 0');
    }

    const now = new Date();
    return new Order({
      orderNo: params.orderNo,
      userId: params.userId,
      totalAmount: params.totalAmount,
      status: OrderStatus.PENDING,
      createdAt: now,
      updatedAt: now,
    });
  }

  static restore(props: {
    id: number;
    orderNo: string;
    userId: number;
    totalAmount: number;
    status: OrderStatus;
    createdAt: Date;
    updatedAt: Date;
  }): Order {
    return new Order(props);
  }

  pay(): void {
    if (this._status !== OrderStatus.PENDING) {
      throw new Error('只有待支付订单可以支付');
    }
    this._status = OrderStatus.PAID;
    this._updatedAt = new Date();
  }

  ship(): void {
    if (this._status !== OrderStatus.PAID) {
      throw new Error('只有已支付订单可以发货');
    }
    this._status = OrderStatus.SHIPPED;
    this._updatedAt = new Date();
  }

  deliver(): void {
    if (this._status !== OrderStatus.SHIPPED) {
      throw new Error('只有已发货订单可以确认送达');
    }
    this._status = OrderStatus.DELIVERED;
    this._updatedAt = new Date();
  }

  cancel(): void {
    if (this._status !== OrderStatus.PENDING) {
      throw new Error('只有待支付订单可以取消');
    }
    this._status = OrderStatus.CANCELLED;
    this._updatedAt = new Date();
  }

  refund(): void {
    if (this._status !== OrderStatus.PAID && this._status !== OrderStatus.SHIPPED) {
      throw new Error('只有已支付或已发货的订单可以退款');
    }
    this._status = OrderStatus.REFUNDED;
    this._updatedAt = new Date();
  }

  canCancel(): boolean {
    return this._status === OrderStatus.PENDING;
  }

  assignId(id: number): void {
    if (this._id) {
      throw new Error('订单 ID 已存在，不能重复赋值');
    }
    this._id = id;
  }

  get id(): number | undefined {
    return this._id;
  }
  get orderNo(): string {
    return this._orderNo;
  }
  get userId(): number {
    return this._userId;
  }
  get totalAmount(): number {
    return this._totalAmount;
  }
  get status(): OrderStatus {
    return this._status;
  }
  get createdAt(): Date {
    return this._createdAt;
  }
  get updatedAt(): Date {
    return this._updatedAt;
  }
}
