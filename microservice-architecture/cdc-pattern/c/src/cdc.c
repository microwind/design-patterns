#include "func.h"

#include <string.h>

void datastore_init(DataStore *store)
{
    store->count = 0;
}

void cdc_broker_init(CdcBroker *broker)
{
    broker->count = 0;
}

void datastore_create_order(DataStore *store, const char *order_id)
{
    strcpy(store->changes[store->count].change_id, "CHG-");
    strcat(store->changes[store->count].change_id, order_id);
    strcpy(store->changes[store->count].aggregate_id, order_id);
    strcpy(store->changes[store->count].change_type, "order_created");
    store->changes[store->count].processed = 0;
    store->count++;
}

void datastore_relay_changes(DataStore *store, CdcBroker *broker)
{
    for (int i = 0; i < store->count; i++) {
        if (!store->changes[i].processed) {
            strcpy(broker->published[broker->count++], store->changes[i].change_id);
            store->changes[i].processed = 1;
        }
    }
}
