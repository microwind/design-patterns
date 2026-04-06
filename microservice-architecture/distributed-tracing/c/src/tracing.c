#include "func.h"

#include <string.h>

void gateway_entry(const char *trace_id, TraceContext *out_context)
{
    strcpy(out_context->trace_id, trace_id);
    strcpy(out_context->span_id, "SPAN-GATEWAY");
    strcpy(out_context->parent_span_id, "");
    strcpy(out_context->service_name, "gateway");
}

void child_span(const TraceContext *parent, const char *service_name, const char *span_id, TraceContext *out_context)
{
    strcpy(out_context->trace_id, parent->trace_id);
    strcpy(out_context->span_id, span_id);
    strcpy(out_context->parent_span_id, parent->span_id);
    strcpy(out_context->service_name, service_name);
}
