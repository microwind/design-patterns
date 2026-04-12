"""
tracing.py - 分布式链路追踪模式（Distributed Tracing）的 Python 实现

【设计模式】
  - 责任链模式：请求沿调用链传播，每个节点创建 span 并传递上下文。
  - 装饰器模式：实际工程中追踪逻辑通过装饰器/@trace 注解包裹业务代码。

【架构思想】
  链路追踪的第一步是上下文传播：确保 traceId 和 spanId 沿调用链正确传递。

【开源对比】
  - OpenTelemetry Python SDK：自动埋点 + W3C Trace Context
  - Jaeger Client Python：Uber 的追踪客户端
  本示例用手动创建 TraceContext 简化。
"""

from dataclasses import dataclass


@dataclass
class TraceContext:
    """链路追踪上下文。

    Attributes:
        trace_id: 全局追踪ID，串联整条调用链
        span_id: 当前 span ID
        parent_span_id: 父级 span ID（根 span 为空字符串）
        service_name: 当前服务名称
    """
    trace_id: str
    span_id: str
    parent_span_id: str
    service_name: str


def gateway_entry(trace_id: str) -> TraceContext:
    """创建入口 span（网关入口）。生成根 span，parent 为空。"""
    return TraceContext(trace_id, "SPAN-GATEWAY", "", "gateway")


def child_span(parent: TraceContext, service_name: str, span_id: str) -> TraceContext:
    """创建子 span（下游调用）。继承 traceId，记录 parentSpanId。"""
    return TraceContext(parent.trace_id, span_id, parent.span_id, service_name)
