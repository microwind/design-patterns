import { Order, OrderStatus } from './order.entity';

describe('Order (领域模型)', () => {
  const create = () =>
    Order.create({ orderNo: 'ORD001', userId: 1, totalAmount: 99.9 });

  it('新建订单状态应为 PENDING', () => {
    const order = create();
    expect(order.status).toBe(OrderStatus.PENDING);
    expect(order.canCancel()).toBe(true);
  });

  it('支付订单后状态应为 PAID', () => {
    const order = create();
    order.pay();
    expect(order.status).toBe(OrderStatus.PAID);
  });

  it('已支付订单不能再次支付', () => {
    const order = create();
    order.pay();
    expect(() => order.pay()).toThrow('只有待支付订单可以支付');
  });

  it('订单状态流转: PENDING -> PAID -> SHIPPED -> DELIVERED', () => {
    const order = create();
    order.pay();
    order.ship();
    order.deliver();
    expect(order.status).toBe(OrderStatus.DELIVERED);
  });

  it('已支付订单可以退款', () => {
    const order = create();
    order.pay();
    order.refund();
    expect(order.status).toBe(OrderStatus.REFUNDED);
  });

  it('订单金额必须大于 0', () => {
    expect(() =>
      Order.create({ orderNo: 'ORD', userId: 1, totalAmount: 0 }),
    ).toThrow('订单金额必须大于 0');
  });
});
