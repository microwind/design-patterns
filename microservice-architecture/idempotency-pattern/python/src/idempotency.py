from dataclasses import dataclass
from typing import Dict, Tuple


@dataclass
class OrderResponse:
    order_id: str
    sku: str
    quantity: int
    status: str
    replayed: bool


class IdempotencyOrderService:
    def __init__(self) -> None:
        self._store: Dict[str, Tuple[str, OrderResponse]] = {}

    def create_order(self, idempotency_key: str, order_id: str, sku: str, quantity: int) -> OrderResponse:
        fingerprint = f"{order_id}|{sku}|{quantity}"
        existing = self._store.get(idempotency_key)
        if existing is not None:
            stored_fingerprint, stored_response = existing
            if stored_fingerprint != fingerprint:
                return OrderResponse(order_id, sku, quantity, "CONFLICT", False)
            return OrderResponse(
                stored_response.order_id,
                stored_response.sku,
                stored_response.quantity,
                stored_response.status,
                True,
            )

        response = OrderResponse(order_id, sku, quantity, "CREATED", False)
        self._store[idempotency_key] = (fingerprint, response)
        return response
