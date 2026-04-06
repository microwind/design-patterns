#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    TraceContext gateway = {0};
    TraceContext order = {0};
    TraceContext inventory = {0};

    gateway_entry("TRACE-1001", &gateway);
    child_span(&gateway, "order-service", "SPAN-ORDER", &order);
    child_span(&order, "inventory-service", "SPAN-INVENTORY", &inventory);

    assert(strcmp(gateway.trace_id, order.trace_id) == 0);
    assert(strcmp(order.trace_id, inventory.trace_id) == 0);
    assert(strcmp(gateway.span_id, order.parent_span_id) == 0);
    assert(strcmp(order.span_id, inventory.parent_span_id) == 0);

    printf("distributed-tracing(c) tests passed\n");
    return 0;
}
