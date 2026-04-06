#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    SagaCoordinator success;
    saga_init(&success, 10, 0);
    SagaOrder completed = saga_execute(&success, "ORD-1001", "SKU-BOOK", 2);
    assert(strcmp(completed.status, "COMPLETED") == 0);
    assert(success.inventory.book_stock == 8);

    SagaCoordinator failure;
    saga_init(&failure, 10, 1);
    SagaOrder cancelled = saga_execute(&failure, "ORD-1002", "SKU-BOOK", 2);
    assert(strcmp(cancelled.status, "CANCELLED") == 0);
    assert(failure.inventory.book_stock == 10);

    printf("distributed-transactions(c) tests passed\n");
    return 0;
}
