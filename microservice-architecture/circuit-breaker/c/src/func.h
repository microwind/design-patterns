/**
 * @file func.h - 断路器模式（Circuit Breaker Pattern）的 C 语言头文件
 *
 * 定义了断路器的数据结构和函数接口。
 *
 * 【设计模式】
 *   - 状态模式（State Pattern）：通过 state 字符串字段实现状态切换。
 *     C 语言没有类和继承，因此用结构体 + 函数指针（或条件分支）来模拟状态模式。
 *   - 代理模式（Proxy Pattern）：断路器函数包裹在真实服务调用之外，
 *     调用方通过 breaker 函数间接控制调用流程。
 *
 * 【架构思想】
 *   断路器防止调用方在下游故障时持续重试导致级联雪崩。
 *
 * 【开源对比】
 *   C 语言生态中断路器通常嵌入在网络框架或 Service Mesh 的 sidecar 中实现，
 *   如 Envoy（C++编写）内置断路器功能。本示例展示纯 C 实现的状态机骨架。
 */

#ifndef CIRCUIT_BREAKER_C_FUNC_H
#define CIRCUIT_BREAKER_C_FUNC_H

/**
 * CircuitBreaker - 断路器状态机结构体
 *
 * 状态转换：
 *   closed → open（连续失败达到阈值）
 *   open → half-open → closed（探测成功）
 *   open → half-open → open（探测失败）
 */
typedef struct {
    int failure_threshold;  /* 触发熔断的连续失败阈值 */
    int failures;           /* 当前连续失败次数 */
    char state[16];         /* 当前状态字符串：closed / open / half-open */
} CircuitBreaker;

/**
 * 初始化断路器，设置失败阈值，初始状态为 "closed"。
 *
 * @param breaker           指向断路器结构体的指针
 * @param failure_threshold 连续失败多少次后触发熔断
 */
void breaker_init(CircuitBreaker *breaker, int failure_threshold);

/**
 * 记录一次失败调用。
 * 仅在 closed 状态下生效：累加失败计数，达到阈值时切换到 open。
 *
 * @param breaker 指向断路器结构体的指针
 */
void breaker_record_failure(CircuitBreaker *breaker);

/**
 * 在 open 状态下进行一次探测调用。
 * 先转为 half-open，再根据探测结果决定最终状态。
 *
 * @param breaker 指向断路器结构体的指针
 * @param success 探测是否成功（1=成功，0=失败）
 */
void breaker_probe(CircuitBreaker *breaker, int success);

#endif
