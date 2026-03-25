import os
import sys

sys.path.append(os.getcwd())

from src.inventory_service import InventoryService
from src.order_service import OrderService


def assert_equal(expected, actual, message):
    if expected != actual:
        raise RuntimeError(f"{message} expected={expected} actual={actual}")


def main():
    inventory = InventoryService()
    service = OrderService(inventory)

    success = service.create_order('ORD-1001', 'SKU-BOOK', 2)
    assert_equal('CREATED', success.status, 'status should be CREATED')
    assert_equal(8, inventory.available('SKU-BOOK'), 'stock should decrease')

    failed = service.create_order('ORD-1002', 'SKU-PEN', 2)
    assert_equal('REJECTED', failed.status, 'status should be REJECTED')
    assert_equal(1, inventory.available('SKU-PEN'), 'stock should remain')

    print('microservice-basics(python) tests passed')


if __name__ == '__main__':
    main()
