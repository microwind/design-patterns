/**
 * @file func.h - 变更数据捕获（CDC Pattern）的 C 语言头文件
 *
 * 【设计模式】
 *   - 观察者模式：Broker 接收变更事件并分发给下游。
 *   - 代理模式：datastore_relay_changes 函数作为中间代理。
 *
 * 【架构思想】
 *   C 语言用固定数组模拟变更日志和消息代理，processed 标记已处理变更。
 */

#ifndef CDC_PATTERN_C_FUNC_H
#define CDC_PATTERN_C_FUNC_H

/** 变更记录结构体 */
typedef struct {
    char change_id[32];      /* 变更ID */
    char aggregate_id[32];   /* 聚合根ID */
    char change_type[32];    /* 变更类型（如 order_created） */
    int processed;           /* 是否已处理（1=是，0=否） */
} ChangeRecord;

/** 数据存储结构体（模拟数据库） */
typedef struct {
    ChangeRecord changes[8]; /* 变更记录数组 */
    int count;               /* 已记录变更数 */
} DataStore;

/** 消息代理结构体（模拟 Kafka / RabbitMQ） */
typedef struct {
    char published[8][32];   /* 已发布的变更ID列表 */
    int count;               /* 已发布数量 */
} CdcBroker;

/** 初始化数据存储 */
void datastore_init(DataStore *store);

/** 初始化消息代理 */
void cdc_broker_init(CdcBroker *broker);

/** 创建订单，同时追加一条 order_created 变更记录 */
void datastore_create_order(DataStore *store, const char *order_id);

/**
 * Connector：扫描未处理变更，发布到 Broker 并标记为已处理。
 */
void datastore_relay_changes(DataStore *store, CdcBroker *broker);

#endif
