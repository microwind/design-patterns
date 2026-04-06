#include "func.h"

#include <string.h>

void rr_init(RoundRobinBalancer *balancer, const Backend *backends, int count)
{
    balancer->count = count;
    balancer->next_index = 0;
    for (int i = 0; i < count; i++) {
        balancer->backends[i] = backends[i];
    }
}

const Backend *rr_next(RoundRobinBalancer *balancer)
{
    const Backend *backend = &balancer->backends[balancer->next_index % balancer->count];
    balancer->next_index++;
    return backend;
}

void wrr_init(WeightedRoundRobinBalancer *balancer, const Backend *backends, int count)
{
    balancer->count = 0;
    balancer->next_index = 0;
    for (int i = 0; i < count; i++) {
        int repeat = backends[i].weight > 0 ? backends[i].weight : 1;
        for (int j = 0; j < repeat; j++) {
            balancer->sequence[balancer->count++] = backends[i];
        }
    }
}

const Backend *wrr_next(WeightedRoundRobinBalancer *balancer)
{
    const Backend *backend = &balancer->sequence[balancer->next_index % balancer->count];
    balancer->next_index++;
    return backend;
}

void lc_init(LeastConnectionsBalancer *balancer, const Backend *backends, int count)
{
    balancer->count = count;
    for (int i = 0; i < count; i++) {
        balancer->backends[i] = backends[i];
    }
}

Backend *lc_acquire(LeastConnectionsBalancer *balancer)
{
    Backend *chosen = &balancer->backends[0];
    for (int i = 1; i < balancer->count; i++) {
        if (balancer->backends[i].active_connections < chosen->active_connections) {
            chosen = &balancer->backends[i];
        }
    }
    chosen->active_connections++;
    return chosen;
}

void lc_release(LeastConnectionsBalancer *balancer, const char *backend_id)
{
    for (int i = 0; i < balancer->count; i++) {
        if (strcmp(balancer->backends[i].backend_id, backend_id) == 0 &&
            balancer->backends[i].active_connections > 0) {
            balancer->backends[i].active_connections--;
        }
    }
}
