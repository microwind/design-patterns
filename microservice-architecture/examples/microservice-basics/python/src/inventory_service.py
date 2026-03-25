from src.inventory_client import InventoryClient


class InventoryService(InventoryClient):
    def __init__(self):
        self.stock = {
            'SKU-BOOK': 10,
            'SKU-PEN': 1,
        }

    def reserve(self, sku, quantity):
        available = self.stock.get(sku, 0)
        if available < quantity:
            return False
        self.stock[sku] = available - quantity
        return True

    def available(self, sku):
        return self.stock.get(sku, 0)
