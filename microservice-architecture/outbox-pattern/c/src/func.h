/**
 * @file func.h - Outbox 模式（Outbox Pattern）的 C 语言头文件
 *
 * 【设计模式】
 *   - 命令模式：OutboxEvent 将事件封装为结构体数据对象。
 *   - 观察者模式：relay 扫描 outbox 并发布到 broker。
 *
 * 【架构思想】
 *   C 语言用固定数组模拟 outbox 表和 broker 的 published 列表。
 */

#ifndef OUTBOX_PATTERN_C_FUNC_H
#define OUTBOX_PATTERN_C_FUNC_H

/** 最大记录数 */
#define MAX_OUTBOX_ORDERS 8

/** 订单结构体 */
typedef struct {
    char order_id[32];  /* 订单ID */
    char status[16];    /* 订单状态 */
} OutboxOrder;

/** Outbox 事件结构体 */
typedef struct {
    char event_id[32];      /* 事件唯一ID */
    char aggregate_id[32];  /* 聚合根ID */
    char event_type[32];    /* 事件类型 */
    char status[16];        /* 发布状态：pending / published */
} OutboxEvent;

/** Outbox 服务结构体 */
typedef struct {
    OutboxOrder orders[MAX_OUTBOX_ORDERS];  /* 模拟 orders 表 */
    int order_count;
    OutboxEvent outbox[MAX_OUTBOX_ORDERS];  /* 模拟 outbox 表 */
    int outbox_count;
} OutboxService;

/** 内存消息代理（模拟 Kafka / RabbitMQ） */
typedef struct {
    char published[16][32]; /* 已发布的事件ID列表 */
    int count;
} MemoryBroker;

/** 初始化 outbox 服务 */
void outbox_service_init(OutboxService *service);
/** 初始化消息代理 */
void broker_init(MemoryBroker *broker);
/** 创建订单（同时写入 orders 和 outbox） */
void outbox_create_order(OutboxService *service, const char *order_id);
/** relay 中继：扫描 pending 事件，发布后标记 published */
void outbox_relay_pending(OutboxService *service, MemoryBroker *broker);

#endif
