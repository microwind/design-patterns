class Order(object):
    def __init__(self, order_id, sku, quantity, status):
        self.order_id = order_id
        self.sku = sku
        self.quantity = quantity
        self.status = status
