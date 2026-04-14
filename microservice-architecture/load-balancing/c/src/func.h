/**
 * @file func.h - 负载均衡模式（Load Balancing Pattern）的 C 语言头文件
 *
 * 定义了三种负载均衡算法的数据结构和函数接口。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：rr_next / wrr_next / lc_acquire 分别实现
 *     不同的负载均衡策略。C 语言通过不同的结构体和函数来模拟策略模式。
 *   - 迭代器模式（Iterator Pattern）：rr_next 和 wrr_next 通过 next_index
 *     取模实现循环迭代。
 *
 * 【架构思想】
 *   负载均衡将流量分散到多个后端实例，避免单点过载。
 *
 * 【开源对比】
 *   C 语言生态中负载均衡通常嵌入在代理服务器（如 Nginx、HAProxy，均为 C 编写）
 *   或 Service Mesh sidecar 中实现。本示例展示纯 C 实现的算法骨架。
 */

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
