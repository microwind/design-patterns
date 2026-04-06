#include "func.h"

#include <string.h>

static int reserve_stock(SagaInventoryService *inventory, const char *sku, int quantity)
{
    if (strcmp(sku, "SKU-BOOK") != 0 || quantity <= 0 || inventory->book_stock < quantity) {
        return 0;
    }
    inventory->book_stock -= quantity;
    return 1;
}

static void release_stock(SagaInventoryService *inventory, const char *sku, int quantity)
{
    if (strcmp(sku, "SKU-BOOK") == 0 && quantity > 0) {
        inventory->book_stock += quantity;
    }
}

void saga_init(SagaCoordinator *coordinator, int stock, int payment_fails)
{
    coordinator->inventory.book_stock = stock;
    coordinator->payment.fail = payment_fails;
}

SagaOrder saga_execute(SagaCoordinator *coordinator, const char *order_id, const char *sku, int quantity)
{
    SagaOrder order = {0};
    strcpy(order.order_id, order_id);
    strcpy(order.status, "PENDING");

    if (!reserve_stock(&coordinator->inventory, sku, quantity)) {
        strcpy(order.status, "CANCELLED");
        return order;
    }
    if (coordinator->payment.fail) {
        release_stock(&coordinator->inventory, sku, quantity);
        strcpy(order.status, "CANCELLED");
        return order;
    }
    strcpy(order.status, "COMPLETED");
    return order;
}
