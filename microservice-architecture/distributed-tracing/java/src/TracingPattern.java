package src;

/**
 * TracingPattern - 分布式链路追踪模式的 Java 实现
 *
 * 【设计模式】
 *   - 责任链模式（Chain of Responsibility）：请求沿调用链传播，每个节点创建 span 并传递上下文。
 *   - 装饰器模式（Decorator Pattern）：实际工程中追踪逻辑作为拦截器/过滤器包裹业务代码。
 *
 * 【架构思想】
 *   链路追踪的第一步是上下文传播（Context Propagation）：确保 traceId 和 spanId
 *   沿调用链正确传递。本示例聚焦于上下文的创建和传播逻辑。
 *
 * 【开源对比】
 *   - OpenTelemetry Java SDK：自动埋点 + W3C Trace Context 传播
 *   - Jaeger Client Java：Uber 的链路追踪客户端
 *   - Spring Cloud Sleuth / Micrometer Tracing：Spring 生态的追踪集成
 *   本示例用手动创建 TraceContext 简化，省略了 HTTP Header 传播和自动埋点。
 */
public class TracingPattern {

    /**
     * TraceContext - 链路追踪上下文
     *
     * 核心字段：
     *   traceId      — 全局唯一，串联整条调用链
     *   spanId       — 当前节点的操作标识
     *   parentSpanId — 上游节点的 spanId，构建父子关系
     *   serviceName  — 当前服务名称
     */
    public static class TraceContext {
        /** 全局追踪ID，串联整条调用链 */
        private final String traceId;
        /** 当前 span ID */
        private final String spanId;
        /** 父级 span ID（根 span 为空字符串） */
        private final String parentSpanId;
        /** 当前服务名称 */
        private final String serviceName;

        public TraceContext(String traceId, String spanId, String parentSpanId, String serviceName) {
            this.traceId = traceId;
            this.spanId = spanId;
            this.parentSpanId = parentSpanId;
            this.serviceName = serviceName;
        }

        public String getTraceId() { return traceId; }
        public String getSpanId() { return spanId; }
        public String getParentSpanId() { return parentSpanId; }
    }

    /**
     * 创建入口 span（网关入口）。
     * 生成 traceId 和根 span，parentSpanId 为空。
     *
     * @param traceId 追踪ID（实际工程中由 SDK 自动生成 128-bit 随机 ID）
     * @return 网关的 TraceContext
     */
    public static TraceContext gatewayEntry(String traceId) {
        return new TraceContext(traceId, "SPAN-GATEWAY", "", "gateway");
    }

    /**
     * 创建子 span（下游服务调用）。
     * 继承父级的 traceId，生成新的 spanId，记录 parentSpanId。
     *
     * @param parent      父级上下文
     * @param serviceName 当前服务名称
     * @param spanId      当前 span ID
     * @return 子服务的 TraceContext
     */
    public static TraceContext childSpan(TraceContext parent, String serviceName, String spanId) {
        return new TraceContext(parent.getTraceId(), spanId, parent.getSpanId(), serviceName);
    }
}
