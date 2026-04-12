/**
 * @file outbox.c - Outbox 模式（Outbox Pattern）的 C 语言实现
 *
 * 【设计模式】
 *   - 命令模式：OutboxEvent 将事件封装为结构体。
 *   - 观察者模式：relay 扫描 outbox 并发布到 broker。
 *
 * 【架构思想】
 *   C 语言用数组模拟数据库表，strcpy/strcmp 操作字符串字段。
 *
 * 【开源对比】
 *   实际工程中 outbox 表存储在 MySQL/PostgreSQL，
 *   relay 通过 SELECT ... WHERE status='pending' FOR UPDATE 扫描。
 */

#include "func.h"

#include <string.h>

/** 初始化 outbox 服务 */
void outbox_service_init(OutboxService *service)
{
    service->order_count = 0;
    service->outbox_count = 0;
}

/** 初始化消息代理 */
void broker_init(MemoryBroker *broker)
{
    broker->count = 0;
}

/**
 * 创建订单。同时写入 orders 和 outbox（模拟同一数据库事务）。
 */
void outbox_create_order(OutboxService *service, const char *order_id)
{
    /* 写入订单 */
    strcpy(service->orders[service->order_count].order_id, order_id);
    strcpy(service->orders[service->order_count].status, "CREATED");
    service->order_count++;

    /* 写入 outbox 事件（同一"事务"） */
    strcpy(service->outbox[service->outbox_count].event_id, "EVT-");
    strcat(service->outbox[service->outbox_count].event_id, order_id);
    strcpy(service->outbox[service->outbox_count].aggregate_id, order_id);
    strcpy(service->outbox[service->outbox_count].event_type, "order_created");
    strcpy(service->outbox[service->outbox_count].status, "pending");
    service->outbox_count++;
}

/**
 * relay 中继：扫描 pending 事件，发布到 broker 后标记 published。
 * 已标记 published 的事件不会被重复发布（安全重跑）。
 */
void outbox_relay_pending(OutboxService *service, MemoryBroker *broker)
{
    for (int i = 0; i < service->outbox_count; i++) {
        if (strcmp(service->outbox[i].status, "pending") == 0) {
            /* 发布到消息中间件 */
            strcpy(broker->published[broker->count++], service->outbox[i].event_id);
            /* 标记为已发布 */
            strcpy(service->outbox[i].status, "published");
        }
    }
}
