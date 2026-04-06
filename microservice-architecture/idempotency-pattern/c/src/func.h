#ifndef IDEMPOTENCY_PATTERN_C_FUNC_H
#define IDEMPOTENCY_PATTERN_C_FUNC_H

#define MAX_RECORDS 8

typedef struct {
    char order_id[32];
    char sku[32];
    int quantity;
    char status[16];
    int replayed;
} IdempotentOrderResponse;

typedef struct {
    char idempotency_key[64];
    char fingerprint[128];
    IdempotentOrderResponse response;
} IdempotencyRecord;

typedef struct {
    IdempotencyRecord records[MAX_RECORDS];
    int count;
} IdempotencyOrderService;

void idempotency_service_init(IdempotencyOrderService *service);
void create_order_with_idempotency(
        IdempotencyOrderService *service,
        const char *idempotency_key,
        const char *order_id,
        const char *sku,
        int quantity,
        IdempotentOrderResponse *out_response
);

#endif
