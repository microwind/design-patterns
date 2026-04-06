package src;

public class TracingPattern {

    public static class TraceContext {
        private final String traceId;
        private final String spanId;
        private final String parentSpanId;
        private final String serviceName;

        public TraceContext(String traceId, String spanId, String parentSpanId, String serviceName) {
            this.traceId = traceId;
            this.spanId = spanId;
            this.parentSpanId = parentSpanId;
            this.serviceName = serviceName;
        }

        public String getTraceId() {
            return traceId;
        }

        public String getSpanId() {
            return spanId;
        }

        public String getParentSpanId() {
            return parentSpanId;
        }
    }

    public static TraceContext gatewayEntry(String traceId) {
        return new TraceContext(traceId, "SPAN-GATEWAY", "", "gateway");
    }

    public static TraceContext childSpan(TraceContext parent, String serviceName, String spanId) {
        return new TraceContext(parent.getTraceId(), spanId, parent.getSpanId(), serviceName);
    }
}
