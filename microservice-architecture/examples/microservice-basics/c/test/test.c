#include "../src/func.h"
#include <assert.h>

int main(void)
{
    InventoryService inventory;
    OrderService order_service;

    inventory_service_init(&inventory);
    order_service_init(&order_service, &inventory);

    Order success = order_service.create_order(&order_service, "ORD-1001", "SKU-BOOK", 2);
    assert(strcmp(success.status, "CREATED") == 0);
    assert(inventory.available(&inventory, "SKU-BOOK") == 8);

    Order failed = order_service.create_order(&order_service, "ORD-1002", "SKU-PEN", 2);
    assert(strcmp(failed.status, "REJECTED") == 0);
    assert(inventory.available(&inventory, "SKU-PEN") == 1);

    printf("microservice-basics(c) tests passed\n");
    return 0;
}
