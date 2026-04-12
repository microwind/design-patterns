/**
 * @file tracing.ts - 分布式链路追踪模式（Distributed Tracing）的 TypeScript 实现
 *
 * 【设计模式】
 *   - 责任链模式：请求沿调用链传播，每个节点创建 span 并传递上下文。
 *   - 装饰器模式：实际工程中追踪逻辑通过中间件/装饰器包裹业务代码。
 *
 * 【架构思想】
 *   TypeScript 通过 TraceContext 类型保证上下文字段的编译期安全。
 *
 * 【开源对比】
 *   - OpenTelemetry JS/TS SDK：原生 TypeScript 支持
 *   本示例用手动创建 TraceContext 简化。
 */

/** 链路追踪上下文类型 */
export type TraceContext = {
  traceId: string;       // 全局追踪ID
  spanId: string;        // 当前 span ID
  parentSpanId: string;  // 父级 span ID
  serviceName: string;   // 当前服务名称
};

/** 创建入口 span（网关入口）。 */
export function gatewayEntry(traceId: string): TraceContext {
  return {
    traceId,
    spanId: "SPAN-GATEWAY",
    parentSpanId: "",
    serviceName: "gateway"
  };
}

/** 创建子 span（下游调用）。继承 traceId，记录 parentSpanId。 */
export function childSpan(parent: TraceContext, serviceName: string, spanId: string): TraceContext {
  return {
    traceId: parent.traceId,
    spanId,
    parentSpanId: parent.spanId,
    serviceName
  };
}
