export class Order {
  constructor(
    public readonly orderId: string,
    public readonly sku: string,
    public readonly quantity: number,
    public readonly status: 'CREATED' | 'REJECTED'
  ) {}
}
