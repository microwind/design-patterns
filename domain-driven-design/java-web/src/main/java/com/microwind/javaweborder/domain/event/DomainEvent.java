// 领域层(Domain) - 领域事件
//
// 业务上发生某件"值得记录的事"（订单创建、订单取消等）时，
// 由聚合根记录、由应用层在事务边界外发布，用于解耦聚合与限界上下文。
// 具体事件类以"过去时"命名（OrderCreated、OrderCanceled），表示既成事实。
package com.microwind.javaweborder.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {

    // 事件发生时间，构造时固化
    LocalDateTime occurredOn();

    // 事件类型标识，用于发布/订阅/日志聚合
    String eventType();
}
