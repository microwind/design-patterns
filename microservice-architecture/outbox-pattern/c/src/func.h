#ifndef OUTBOX_PATTERN_C_FUNC_H
#define OUTBOX_PATTERN_C_FUNC_H

#define MAX_OUTBOX_ORDERS 8

typedef struct {
    char order_id[32];
    char status[16];
} OutboxOrder;

typedef struct {
    char event_id[32];
    char aggregate_id[32];
    char event_type[32];
    char status[16];
} OutboxEvent;

typedef struct {
    OutboxOrder orders[MAX_OUTBOX_ORDERS];
    int order_count;
    OutboxEvent outbox[MAX_OUTBOX_ORDERS];
    int outbox_count;
} OutboxService;

typedef struct {
    char published[16][32];
    int count;
} MemoryBroker;

void outbox_service_init(OutboxService *service);
void broker_init(MemoryBroker *broker);
void outbox_create_order(OutboxService *service, const char *order_id);
void outbox_relay_pending(OutboxService *service, MemoryBroker *broker);

#endif
