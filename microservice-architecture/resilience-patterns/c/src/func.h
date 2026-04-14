/**
 * @file func.h - 弹性模式组合（Resilience Patterns）的 C 语言头文件
 *
 * 定义了超时、重试、断路器三种弹性模式的数据结构和函数接口。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：call_with_timeout / retry_call / circuit_breaker_execute
 *     是三种可独立使用的弹性策略。
 *   - 代理模式（Proxy Pattern）：弹性函数包裹在真实操作之外。
 *   - 状态模式（State Pattern）：CircuitBreaker 通过 open 标志在不同状态下表现不同行为。
 *
 * 【架构思想】
 *   超时防止无限等待，重试处理暂时性故障，断路器阻止级联雪崩。
 *
 * 【开源对比】
 *   C 语言生态中弹性模式通常嵌入在 Service Mesh sidecar（如 Envoy，C++ 编写）
 *   中实现。本示例展示纯 C 实现的弹性模式骨架。
 */

#ifndef RESILIENCE_PATTERNS_C_FUNC_H
#define RESILIENCE_PATTERNS_C_FUNC_H

typedef struct {
    const char *value;
    int fail;
    int delay_ms;
} ScriptedResult;

typedef struct {
    ScriptedResult results[8];
    int count;
    int index;
} ScriptedDependency;

typedef struct {
    int failure_threshold;
    int consecutive_failures;
    int open;
} CircuitBreaker;

void scripted_dependency_init(ScriptedDependency *dependency, const ScriptedResult *results, int count);
int call_with_timeout(ScriptedDependency *dependency, int timeout_ms, const char **value_out);
int retry_call(ScriptedDependency *dependency, int max_attempts, const char **value_out, int *attempts_out);
void circuit_breaker_init(CircuitBreaker *breaker, int failure_threshold);
int circuit_breaker_execute(CircuitBreaker *breaker, ScriptedDependency *dependency, const char *fallback, const char **value_out, int *circuit_open);
void circuit_breaker_reset(CircuitBreaker *breaker);

#endif
