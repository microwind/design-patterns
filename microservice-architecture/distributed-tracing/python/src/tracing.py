from dataclasses import dataclass


@dataclass
class TraceContext:
    trace_id: str
    span_id: str
    parent_span_id: str
    service_name: str


def gateway_entry(trace_id: str) -> TraceContext:
    return TraceContext(trace_id, "SPAN-GATEWAY", "", "gateway")


def child_span(parent: TraceContext, service_name: str, span_id: str) -> TraceContext:
    return TraceContext(parent.trace_id, span_id, parent.span_id, service_name)
