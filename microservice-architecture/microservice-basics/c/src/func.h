#ifndef MICROSERVICE_BASICS_FUNC_H
#define MICROSERVICE_BASICS_FUNC_H

#include <stdio.h>
#include <string.h>

typedef struct InventoryService InventoryService;
typedef struct OrderService OrderService;
typedef struct Order Order;

struct InventoryService {
    int book_stock;
    int pen_stock;
    int (*reserve)(InventoryService *service, const char *sku, int quantity);
    int (*available)(InventoryService *service, const char *sku);
};

struct Order {
    char order_id[32];
    char sku[32];
    int quantity;
    char status[16];
};

struct OrderService {
    InventoryService *inventory;
    Order (*create_order)(OrderService *service, const char *order_id, const char *sku, int quantity);
};

void inventory_service_init(InventoryService *service);
void order_service_init(OrderService *service, InventoryService *inventory);
int reserve_over_http(const char *host, int port, const char *sku, int quantity);
void create_order_over_http(const char *host, int port, const char *order_id, const char *sku, int quantity, Order *out_order);

#endif
