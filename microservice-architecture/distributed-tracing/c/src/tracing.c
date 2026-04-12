/**
 * @file tracing.c - 分布式链路追踪模式的 C 语言实现
 *
 * 【设计模式】
 *   - 责任链模式：上下文通过结构体指针沿调用链传递。
 *
 * 【架构思想】
 *   C 语言中追踪上下文通常嵌入在请求结构体中传递。
 *   在 Service Mesh 架构中，sidecar（如 Envoy）负责自动传播追踪 Header。
 *
 * 【开源对比】
 *   - Envoy（C++）：自动为经过的请求注入/传播 trace header
 *   - OpenTelemetry C++ SDK：提供 C++ 追踪 API
 *   本示例展示最基础的上下文创建和传播逻辑。
 */

#include "func.h"

#include <string.h>

/**
 * 创建入口 span（网关入口）。
 * 生成根 span，parent_span_id 为空字符串。
 */
void gateway_entry(const char *trace_id, TraceContext *out_context)
{
    strcpy(out_context->trace_id, trace_id);
    strcpy(out_context->span_id, "SPAN-GATEWAY");
    /* 根 span 没有父级 */
    strcpy(out_context->parent_span_id, "");
    strcpy(out_context->service_name, "gateway");
}

/**
 * 创建子 span（下游服务调用）。
 * 继承父级的 trace_id，生成新 span_id，记录 parent_span_id。
 */
void child_span(const TraceContext *parent, const char *service_name, const char *span_id, TraceContext *out_context)
{
    /* 继承全局 traceId */
    strcpy(out_context->trace_id, parent->trace_id);
    /* 新的 spanId */
    strcpy(out_context->span_id, span_id);
    /* 父级的 spanId 成为当前的 parentSpanId */
    strcpy(out_context->parent_span_id, parent->span_id);
    strcpy(out_context->service_name, service_name);
}
