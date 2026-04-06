#ifndef CDC_PATTERN_C_FUNC_H
#define CDC_PATTERN_C_FUNC_H

typedef struct {
    char change_id[32];
    char aggregate_id[32];
    char change_type[32];
    int processed;
} ChangeRecord;

typedef struct {
    ChangeRecord changes[8];
    int count;
} DataStore;

typedef struct {
    char published[8][32];
    int count;
} CdcBroker;

void datastore_init(DataStore *store);
void cdc_broker_init(CdcBroker *broker);
void datastore_create_order(DataStore *store, const char *order_id);
void datastore_relay_changes(DataStore *store, CdcBroker *broker);

#endif
