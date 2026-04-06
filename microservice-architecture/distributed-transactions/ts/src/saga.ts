export type SagaOrder = {
  orderId: string;
  status: string;
};

class InventoryService {
  constructor(public bookStock: number) {}

  reserve(sku: string, quantity: number): boolean {
    if (sku !== "SKU-BOOK" || quantity <= 0 || this.bookStock < quantity) {
      return false;
    }
    this.bookStock -= quantity;
    return true;
  }

  release(sku: string, quantity: number): void {
    if (sku === "SKU-BOOK" && quantity > 0) {
      this.bookStock += quantity;
    }
  }
}

class PaymentService {
  constructor(private readonly fail: boolean) {}

  charge(orderId: string): boolean {
    return !this.fail;
  }
}

export class SagaCoordinator {
  readonly inventory: InventoryService;
  private readonly payment: PaymentService;

  constructor(stock: number, paymentFails: boolean) {
    this.inventory = new InventoryService(stock);
    this.payment = new PaymentService(paymentFails);
  }

  execute(orderId: string, sku: string, quantity: number): SagaOrder {
    const order: SagaOrder = { orderId, status: "PENDING" };
    if (!this.inventory.reserve(sku, quantity)) {
      order.status = "CANCELLED";
      return order;
    }
    if (!this.payment.charge(orderId)) {
      this.inventory.release(sku, quantity);
      order.status = "CANCELLED";
      return order;
    }
    order.status = "COMPLETED";
    return order;
  }
}
