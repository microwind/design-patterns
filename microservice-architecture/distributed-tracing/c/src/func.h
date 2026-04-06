#ifndef DISTRIBUTED_TRACING_C_FUNC_H
#define DISTRIBUTED_TRACING_C_FUNC_H

typedef struct {
    char trace_id[32];
    char span_id[32];
    char parent_span_id[32];
    char service_name[32];
} TraceContext;

void gateway_entry(const char *trace_id, TraceContext *out_context);
void child_span(const TraceContext *parent, const char *service_name, const char *span_id, TraceContext *out_context);

#endif
