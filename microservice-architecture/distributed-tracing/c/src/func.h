/**
 * @file func.h - 分布式链路追踪模式的 C 语言头文件
 *
 * 【设计模式】
 *   - 责任链模式：上下文沿调用链通过指针传递。
 *
 * 【架构思想】
 *   C 语言用结构体传递追踪上下文，类似于 HTTP Header 传播 trace context。
 */

#ifndef DISTRIBUTED_TRACING_C_FUNC_H
#define DISTRIBUTED_TRACING_C_FUNC_H

/** TraceContext - 链路追踪上下文 */
typedef struct {
    char trace_id[32];        /* 全局追踪ID */
    char span_id[32];         /* 当前 span ID */
    char parent_span_id[32];  /* 父级 span ID */
    char service_name[32];    /* 当前服务名称 */
} TraceContext;

/** 创建入口 span（网关入口） */
void gateway_entry(const char *trace_id, TraceContext *out_context);

/** 创建子 span（下游调用），继承 traceId，记录 parentSpanId */
void child_span(const TraceContext *parent, const char *service_name, const char *span_id, TraceContext *out_context);

#endif
