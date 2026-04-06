export type TraceContext = {
  traceId: string;
  spanId: string;
  parentSpanId: string;
  serviceName: string;
};

export function gatewayEntry(traceId: string): TraceContext {
  return {
    traceId,
    spanId: "SPAN-GATEWAY",
    parentSpanId: "",
    serviceName: "gateway"
  };
}

export function childSpan(parent: TraceContext, serviceName: string, spanId: string): TraceContext {
  return {
    traceId: parent.traceId,
    spanId,
    parentSpanId: parent.spanId,
    serviceName
  };
}
