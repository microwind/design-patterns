/**
 * @file tracing.js - 分布式链路追踪模式（Distributed Tracing）的 JavaScript 实现
 *
 * 【设计模式】
 *   - 责任链模式：请求沿调用链传播，每个节点创建 span 并传递上下文。
 *   - 装饰器模式：实际工程中追踪逻辑通过中间件包裹业务代码。
 *
 * 【架构思想】
 *   链路追踪的第一步是上下文传播：确保 traceId 和 spanId 沿调用链正确传递。
 *
 * 【开源对比】
 *   - OpenTelemetry JS SDK：自动埋点 + W3C Trace Context
 *   本示例用手动创建 context 对象简化。
 */

/**
 * 创建入口 span（网关入口）。
 * @param {string} traceId 追踪ID
 * @returns {{ traceId, spanId, parentSpanId, serviceName }} 网关上下文
 */
export function gatewayEntry(traceId) {
  return {
    traceId,
    spanId: 'SPAN-GATEWAY',
    parentSpanId: '',
    serviceName: 'gateway'
  }
}

/**
 * 创建子 span（下游服务调用）。
 * 继承父级的 traceId，生成新 spanId，记录 parentSpanId。
 * @param {object} parent      父级上下文
 * @param {string} serviceName 当前服务名称
 * @param {string} spanId      当前 span ID
 * @returns {{ traceId, spanId, parentSpanId, serviceName }} 子服务上下文
 */
export function childSpan(parent, serviceName, spanId) {
  return {
    traceId: parent.traceId,
    spanId,
    parentSpanId: parent.spanId,
    serviceName
  }
}
