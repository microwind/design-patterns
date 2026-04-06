#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    ServiceRegistry registry;
    registry_init(&registry);
    registry_register(&registry, "inventory-a", "10.0.0.1:8081");
    registry_register(&registry, "inventory-b", "10.0.0.2:8081");

    assert(registry.count == 2);
    assert(strcmp(registry_next(&registry)->instance_id, "inventory-a") == 0);
    assert(strcmp(registry_next(&registry)->instance_id, "inventory-b") == 0);
    assert(strcmp(registry_next(&registry)->instance_id, "inventory-a") == 0);

    assert(registry_deregister(&registry, "inventory-a") == 1);
    assert(registry.count == 1);
    assert(strcmp(registry_next(&registry)->instance_id, "inventory-b") == 0);

    printf("service-discovery(c) tests passed\n");
    return 0;
}
