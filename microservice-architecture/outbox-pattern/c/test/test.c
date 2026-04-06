#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    OutboxService service;
    MemoryBroker broker;
    outbox_service_init(&service);
    broker_init(&broker);

    outbox_create_order(&service, "ORD-1001");
    assert(service.order_count == 1);
    assert(strcmp(service.outbox[0].status, "pending") == 0);

    outbox_relay_pending(&service, &broker);
    assert(broker.count == 1);
    assert(strcmp(service.outbox[0].status, "published") == 0);

    outbox_relay_pending(&service, &broker);
    assert(broker.count == 1);

    printf("outbox-pattern(c) tests passed\n");
    return 0;
}
