export type OrderResponse = {
  orderId: string;
  sku: string;
  quantity: number;
  status: string;
  replayed: boolean;
};

type StoredResult = {
  fingerprint: string;
  response: OrderResponse;
};

export class IdempotencyOrderService {
  private store: Record<string, StoredResult> = {};

  createOrder(idempotencyKey: string, orderId: string, sku: string, quantity: number): OrderResponse {
    const fingerprint = `${orderId}|${sku}|${quantity}`;
    const existing = this.store[idempotencyKey];
    if (existing) {
      if (existing.fingerprint !== fingerprint) {
        return { orderId, sku, quantity, status: "CONFLICT", replayed: false };
      }
      return { ...existing.response, replayed: true };
    }

    const response: OrderResponse = { orderId, sku, quantity, status: "CREATED", replayed: false };
    this.store[idempotencyKey] = { fingerprint, response };
    return response;
  }
}
