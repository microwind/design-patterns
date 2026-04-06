#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    IdempotencyOrderService service;
    idempotency_service_init(&service);

    IdempotentOrderResponse first = {0};
    create_order_with_idempotency(&service, "IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1, &first);
    assert(strcmp(first.status, "CREATED") == 0);
    assert(first.replayed == 0);

    IdempotentOrderResponse second = {0};
    create_order_with_idempotency(&service, "IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1, &second);
    assert(strcmp(second.status, "CREATED") == 0);
    assert(second.replayed == 1);

    IdempotentOrderResponse conflict = {0};
    create_order_with_idempotency(&service, "IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 2, &conflict);
    assert(strcmp(conflict.status, "CONFLICT") == 0);
    assert(conflict.replayed == 0);

    printf("idempotency-pattern(c) tests passed\n");
    return 0;
}
