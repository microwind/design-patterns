#include "func.h"

#include <string.h>

void outbox_service_init(OutboxService *service)
{
    service->order_count = 0;
    service->outbox_count = 0;
}

void broker_init(MemoryBroker *broker)
{
    broker->count = 0;
}

void outbox_create_order(OutboxService *service, const char *order_id)
{
    strcpy(service->orders[service->order_count].order_id, order_id);
    strcpy(service->orders[service->order_count].status, "CREATED");
    service->order_count++;

    strcpy(service->outbox[service->outbox_count].event_id, "EVT-");
    strcat(service->outbox[service->outbox_count].event_id, order_id);
    strcpy(service->outbox[service->outbox_count].aggregate_id, order_id);
    strcpy(service->outbox[service->outbox_count].event_type, "order_created");
    strcpy(service->outbox[service->outbox_count].status, "pending");
    service->outbox_count++;
}

void outbox_relay_pending(OutboxService *service, MemoryBroker *broker)
{
    for (int i = 0; i < service->outbox_count; i++) {
        if (strcmp(service->outbox[i].status, "pending") == 0) {
            strcpy(broker->published[broker->count++], service->outbox[i].event_id);
            strcpy(service->outbox[i].status, "published");
        }
    }
}
