from src.order import Order


class OrderService(object):
    def __init__(self, inventory_client):
        self.inventory_client = inventory_client

    def create_order(self, order_id, sku, quantity):
        if self.inventory_client.reserve(sku, quantity):
            return Order(order_id, sku, quantity, 'CREATED')
        return Order(order_id, sku, quantity, 'REJECTED')
