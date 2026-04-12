// Package src 实现了分布式链路追踪模式（Distributed Tracing）的核心逻辑。
//
// 【设计模式】
//   - 责任链模式：请求沿调用链传播，每个节点创建 span 并传递上下文。
//   - 装饰器模式：实际工程中追踪逻辑作为中间件包裹业务代码。
//
// 【架构思想】
//   链路追踪的第一步是上下文传播：确保 traceId 和 spanId 沿调用链正确传递。
//
// 【开源对比】
//   - OpenTelemetry Go SDK：自动埋点 + W3C Trace Context 传播
//   - Jaeger Client Go：Uber 的追踪客户端
//   本示例用手动创建 TraceContext 简化。
package src

// TraceContext 链路追踪上下文。
// TraceID 串联整条调用链，SpanID/ParentSpanID 构建父子关系。
type TraceContext struct {
	TraceID      string // 全局追踪ID
	SpanID       string // 当前 span ID
	ParentSpanID string // 父级 span ID（根 span 为空）
	ServiceName  string // 当前服务名称
}

// GatewayEntry 创建入口 span（网关入口）。
// 生成 traceId 和根 span，parentSpanId 为空。
func GatewayEntry(traceID string) TraceContext {
	return TraceContext{
		TraceID:      traceID,
		SpanID:       "SPAN-GATEWAY",
		ParentSpanID: "",
		ServiceName:  "gateway",
	}
}

// ChildSpan 创建子 span（下游服务调用）。
// 继承父级的 traceId，生成新 spanId，记录 parentSpanId。
func ChildSpan(parent TraceContext, serviceName string, spanID string) TraceContext {
	return TraceContext{
		TraceID:      parent.TraceID,
		SpanID:       spanID,
		ParentSpanID: parent.SpanID,
		ServiceName:  serviceName,
	}
}
