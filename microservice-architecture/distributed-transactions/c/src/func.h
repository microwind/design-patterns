#ifndef DISTRIBUTED_TRANSACTIONS_C_FUNC_H
#define DISTRIBUTED_TRANSACTIONS_C_FUNC_H

typedef struct {
    char order_id[32];
    char status[16];
} SagaOrder;

typedef struct {
    int book_stock;
} SagaInventoryService;

typedef struct {
    int fail;
} SagaPaymentService;

typedef struct {
    SagaInventoryService inventory;
    SagaPaymentService payment;
} SagaCoordinator;

void saga_init(SagaCoordinator *coordinator, int stock, int payment_fails);
SagaOrder saga_execute(SagaCoordinator *coordinator, const char *order_id, const char *sku, int quantity);

#endif
