#ifndef SERVICE_DISCOVERY_C_FUNC_H
#define SERVICE_DISCOVERY_C_FUNC_H

#define MAX_INSTANCES 8

typedef struct {
    char instance_id[32];
    char address[64];
} ServiceInstance;

typedef struct {
    ServiceInstance instances[MAX_INSTANCES];
    int count;
    int next_index;
} ServiceRegistry;

void registry_init(ServiceRegistry *registry);
void registry_register(ServiceRegistry *registry, const char *instance_id, const char *address);
int registry_deregister(ServiceRegistry *registry, const char *instance_id);
const ServiceInstance *registry_next(ServiceRegistry *registry);

#endif
