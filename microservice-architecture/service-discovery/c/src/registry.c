#include "func.h"

#include <string.h>

void registry_init(ServiceRegistry *registry)
{
    registry->count = 0;
    registry->next_index = 0;
}

void registry_register(ServiceRegistry *registry, const char *instance_id, const char *address)
{
    strcpy(registry->instances[registry->count].instance_id, instance_id);
    strcpy(registry->instances[registry->count].address, address);
    registry->count++;
}

int registry_deregister(ServiceRegistry *registry, const char *instance_id)
{
    for (int i = 0; i < registry->count; i++) {
        if (strcmp(registry->instances[i].instance_id, instance_id) == 0) {
            for (int j = i; j < registry->count - 1; j++) {
                registry->instances[j] = registry->instances[j + 1];
            }
            registry->count--;
            if (registry->next_index >= registry->count) {
                registry->next_index = 0;
            }
            return 1;
        }
    }
    return 0;
}

const ServiceInstance *registry_next(ServiceRegistry *registry)
{
    if (registry->count == 0) {
        return NULL;
    }
    const ServiceInstance *instance = &registry->instances[registry->next_index % registry->count];
    registry->next_index++;
    return instance;
}
