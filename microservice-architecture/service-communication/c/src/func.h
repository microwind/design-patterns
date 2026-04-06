#ifndef SERVICE_COMMUNICATION_C_FUNC_H
#define SERVICE_COMMUNICATION_C_FUNC_H

#define MAX_ORDERS 8
#define MAX_EVENTS 8

typedef struct {
    char order_id[32];
    char sku[32];
    int quantity;
    char status[32];
} CommOrder;

typedef struct {
    int book_stock;
} CommInventoryService;

typedef struct {
    char fail_order_id[32];
} CommPaymentService;

typedef struct {
    CommOrder orders[MAX_ORDERS];
    int count;
} CommOrderStore;

typedef struct {
    char order_id[32];
    char sku[32];
    int quantity;
} OrderPlacedEvent;

typedef struct {
    OrderPlacedEvent events[MAX_EVENTS];
    int count;
} EventQueue;

int comm_reserve(CommInventoryService *inventory, const char *sku, int quantity);
int comm_charge(CommPaymentService *payment, const char *order_id);
CommOrder sync_place_order(CommInventoryService *inventory, CommPaymentService *payment, const char *order_id, const char *sku, int quantity);
void order_store_init(CommOrderStore *store);
void order_store_save(CommOrderStore *store, const CommOrder *order);
CommOrder *order_store_get(CommOrderStore *store, const char *order_id);
void event_queue_init(EventQueue *queue);
void async_place_order(EventQueue *queue, CommOrderStore *store, const char *order_id, const char *sku, int quantity);
void async_drain(EventQueue *queue, CommOrderStore *store, CommInventoryService *inventory, CommPaymentService *payment);

#endif
