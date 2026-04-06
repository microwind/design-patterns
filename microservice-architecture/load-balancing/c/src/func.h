#ifndef LOAD_BALANCING_C_FUNC_H
#define LOAD_BALANCING_C_FUNC_H

#define MAX_BACKENDS 8

typedef struct {
    char backend_id[32];
    int weight;
    int active_connections;
} Backend;

typedef struct {
    Backend backends[MAX_BACKENDS];
    int count;
    int next_index;
} RoundRobinBalancer;

typedef struct {
    Backend sequence[32];
    int count;
    int next_index;
} WeightedRoundRobinBalancer;

typedef struct {
    Backend backends[MAX_BACKENDS];
    int count;
} LeastConnectionsBalancer;

void rr_init(RoundRobinBalancer *balancer, const Backend *backends, int count);
const Backend *rr_next(RoundRobinBalancer *balancer);
void wrr_init(WeightedRoundRobinBalancer *balancer, const Backend *backends, int count);
const Backend *wrr_next(WeightedRoundRobinBalancer *balancer);
void lc_init(LeastConnectionsBalancer *balancer, const Backend *backends, int count);
Backend *lc_acquire(LeastConnectionsBalancer *balancer);
void lc_release(LeastConnectionsBalancer *balancer, const char *backend_id);

#endif
