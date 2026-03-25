#include "func.h"

static Order create_order_impl(OrderService *service, const char *order_id, const char *sku, int quantity)
{
    Order order;
    strcpy(order.order_id, order_id);
    strcpy(order.sku, sku);
    order.quantity = quantity;

    if (service->inventory->reserve(service->inventory, sku, quantity)) {
        strcpy(order.status, "CREATED");
    } else {
        strcpy(order.status, "REJECTED");
    }

    return order;
}

void order_service_init(OrderService *service, InventoryService *inventory)
{
    service->inventory = inventory;
    service->create_order = create_order_impl;
}
