export function gatewayEntry(traceId) {
  return {
    traceId,
    spanId: 'SPAN-GATEWAY',
    parentSpanId: '',
    serviceName: 'gateway'
  }
}

export function childSpan(parent, serviceName, spanId) {
  return {
    traceId: parent.traceId,
    spanId,
    parentSpanId: parent.spanId,
    serviceName
  }
}
