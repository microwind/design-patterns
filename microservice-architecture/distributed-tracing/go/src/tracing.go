package src

type TraceContext struct {
	TraceID      string
	SpanID       string
	ParentSpanID string
	ServiceName  string
}

func GatewayEntry(traceID string) TraceContext {
	return TraceContext{
		TraceID:      traceID,
		SpanID:       "SPAN-GATEWAY",
		ParentSpanID: "",
		ServiceName:  "gateway",
	}
}

func ChildSpan(parent TraceContext, serviceName string, spanID string) TraceContext {
	return TraceContext{
		TraceID:      parent.TraceID,
		SpanID:       spanID,
		ParentSpanID: parent.SpanID,
		ServiceName:  serviceName,
	}
}
