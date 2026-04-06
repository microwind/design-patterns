#include "func.h"

#include <stdio.h>
#include <string.h>

void idempotency_service_init(IdempotencyOrderService *service)
{
    service->count = 0;
}

void create_order_with_idempotency(
        IdempotencyOrderService *service,
        const char *idempotency_key,
        const char *order_id,
        const char *sku,
        int quantity,
        IdempotentOrderResponse *out_response)
{
    char fingerprint[128];
    snprintf(fingerprint, sizeof(fingerprint), "%s|%s|%d", order_id, sku, quantity);

    for (int i = 0; i < service->count; i++) {
        if (strcmp(service->records[i].idempotency_key, idempotency_key) == 0) {
            if (strcmp(service->records[i].fingerprint, fingerprint) != 0) {
                strcpy(out_response->order_id, order_id);
                strcpy(out_response->sku, sku);
                out_response->quantity = quantity;
                strcpy(out_response->status, "CONFLICT");
                out_response->replayed = 0;
                return;
            }

            *out_response = service->records[i].response;
            out_response->replayed = 1;
            return;
        }
    }

    strcpy(out_response->order_id, order_id);
    strcpy(out_response->sku, sku);
    out_response->quantity = quantity;
    strcpy(out_response->status, "CREATED");
    out_response->replayed = 0;

    strcpy(service->records[service->count].idempotency_key, idempotency_key);
    strcpy(service->records[service->count].fingerprint, fingerprint);
    service->records[service->count].response = *out_response;
    service->count++;
}
