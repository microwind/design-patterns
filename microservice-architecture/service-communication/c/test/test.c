#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    CommInventoryService sync_inventory = {5};
    CommPaymentService sync_payment = {""};
    CommOrder sync_order = sync_place_order(&sync_inventory, &sync_payment, "ORD-1001", "SKU-BOOK", 2);
    assert(strcmp(sync_order.status, "CREATED") == 0);

    EventQueue queue;
    CommOrderStore store;
    CommInventoryService async_inventory = {5};
    CommPaymentService async_payment = {""};
    event_queue_init(&queue);
    order_store_init(&store);
    async_place_order(&queue, &store, "ORD-2001", "SKU-BOOK", 2);
    assert(strcmp(order_store_get(&store, "ORD-2001")->status, "PENDING") == 0);
    async_drain(&queue, &store, &async_inventory, &async_payment);
    assert(strcmp(order_store_get(&store, "ORD-2001")->status, "CREATED") == 0);

    EventQueue failing_queue;
    CommOrderStore failing_store;
    CommInventoryService failing_inventory = {5};
    CommPaymentService failing_payment = {"ORD-2002"};
    event_queue_init(&failing_queue);
    order_store_init(&failing_store);
    async_place_order(&failing_queue, &failing_store, "ORD-2002", "SKU-BOOK", 1);
    async_drain(&failing_queue, &failing_store, &failing_inventory, &failing_payment);
    assert(strcmp(order_store_get(&failing_store, "ORD-2002")->status, "PAYMENT_FAILED") == 0);

    printf("service-communication(c) tests passed\n");
    return 0;
}
