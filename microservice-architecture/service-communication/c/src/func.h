/**
 * @file func.h - 服务间通信模式（Service Communication Pattern）的 C 语言头文件
 *
 * 定义了同步/异步通信模式的数据结构和函数接口。
 *
 * 【设计模式】
 *   - 外观模式（Facade Pattern）：sync_place_order 将库存检查和支付调用封装为
 *     统一的下单接口，调用方无需了解内部编排逻辑。
 *   - 观察者模式（Observer Pattern）：EventQueue + async_drain 模拟发布-订阅机制，
 *     async_place_order 发布事件，async_drain 消费事件并驱动下游处理。
 *   - 中介者模式（Mediator Pattern）：EventQueue 充当中介者，协调多服务间交互。
 *
 * 【架构思想】
 *   同步通信简单直观但耦合度高；异步通信通过事件队列解耦，支持独立扩展和故障隔离。
 *   C 语言用结构体 + 函数来模拟面向对象的服务通信模式。
 *
 * 【开源对比】
 *   C 语言生态中服务通信通常通过网络库（如 libcurl、libuv）或消息队列客户端
 *   （如 librdkafka、rabbitmq-c）实现。本示例展示纯 C 实现的通信模式骨架。
 */

#ifndef SERVICE_COMMUNICATION_C_FUNC_H
#define SERVICE_COMMUNICATION_C_FUNC_H

#define MAX_ORDERS 8
#define MAX_EVENTS 8

typedef struct {
    char order_id[32];
    char sku[32];
    int quantity;
    char status[32];
} CommOrder;

typedef struct {
    int book_stock;
} CommInventoryService;

typedef struct {
    char fail_order_id[32];
} CommPaymentService;

typedef struct {
    CommOrder orders[MAX_ORDERS];
    int count;
} CommOrderStore;

typedef struct {
    char order_id[32];
    char sku[32];
    int quantity;
} OrderPlacedEvent;

typedef struct {
    OrderPlacedEvent events[MAX_EVENTS];
    int count;
} EventQueue;

int comm_reserve(CommInventoryService *inventory, const char *sku, int quantity);
int comm_charge(CommPaymentService *payment, const char *order_id);
CommOrder sync_place_order(CommInventoryService *inventory, CommPaymentService *payment, const char *order_id, const char *sku, int quantity);
void order_store_init(CommOrderStore *store);
void order_store_save(CommOrderStore *store, const CommOrder *order);
CommOrder *order_store_get(CommOrderStore *store, const char *order_id);
void event_queue_init(EventQueue *queue);
void async_place_order(EventQueue *queue, CommOrderStore *store, const char *order_id, const char *sku, int quantity);
void async_drain(EventQueue *queue, CommOrderStore *store, CommInventoryService *inventory, CommPaymentService *payment);

#endif
