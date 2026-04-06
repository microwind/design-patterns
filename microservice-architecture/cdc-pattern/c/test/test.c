#include "../src/func.h"

#include <assert.h>
#include <stdio.h>

int main(void)
{
    DataStore store;
    CdcBroker broker;
    datastore_init(&store);
    cdc_broker_init(&broker);
    datastore_create_order(&store, "ORD-1001");
    assert(store.changes[0].processed == 0);
    datastore_relay_changes(&store, &broker);
    assert(broker.count == 1);
    assert(store.changes[0].processed == 1);
    datastore_relay_changes(&store, &broker);
    assert(broker.count == 1);
    printf("cdc-pattern(c) tests passed\n");
    return 0;
}
