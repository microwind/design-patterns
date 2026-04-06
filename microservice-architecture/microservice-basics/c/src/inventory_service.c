#include "func.h"

static int reserve_impl(InventoryService *service, const char *sku, int quantity)
{
    int *stock = NULL;

    if (strcmp(sku, "SKU-BOOK") == 0) {
        stock = &service->book_stock;
    } else if (strcmp(sku, "SKU-PEN") == 0) {
        stock = &service->pen_stock;
    } else {
        return 0;
    }

    if (*stock < quantity) {
        return 0;
    }

    *stock -= quantity;
    return 1;
}

static int available_impl(InventoryService *service, const char *sku)
{
    if (strcmp(sku, "SKU-BOOK") == 0) {
        return service->book_stock;
    }
    if (strcmp(sku, "SKU-PEN") == 0) {
        return service->pen_stock;
    }
    return 0;
}

void inventory_service_init(InventoryService *service)
{
    service->book_stock = 10;
    service->pen_stock = 1;
    service->reserve = reserve_impl;
    service->available = available_impl;
}
