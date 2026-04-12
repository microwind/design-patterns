/**
 * @file func.h - 幂等模式（Idempotency Pattern）的 C 语言头文件
 *
 * 【设计模式】
 *   - 备忘录模式：用 IdempotencyRecord 数组存储首次执行结果。
 *   - 代理模式：create_order_with_idempotency 函数包裹业务逻辑。
 *
 * 【架构思想】
 *   C 语言用固定数组模拟幂等存储，fingerprint 字符串用于冲突检测。
 */

#ifndef IDEMPOTENCY_PATTERN_C_FUNC_H
#define IDEMPOTENCY_PATTERN_C_FUNC_H

/** 最大幂等记录数 */
#define MAX_RECORDS 8

/** 订单响应结构体 */
typedef struct {
    char order_id[32];   /* 订单ID */
    char sku[32];        /* 商品SKU */
    int quantity;        /* 数量 */
    char status[16];     /* 状态：CREATED / CONFLICT */
    int replayed;        /* 是否为重放结果（1=是，0=否） */
} IdempotentOrderResponse;

/** 幂等记录结构体（内部存储） */
typedef struct {
    char idempotency_key[64];  /* 幂等键 */
    char fingerprint[128];     /* 请求指纹（参数拼接） */
    IdempotentOrderResponse response; /* 首次执行的响应 */
} IdempotencyRecord;

/** 幂等订单服务结构体 */
typedef struct {
    IdempotencyRecord records[MAX_RECORDS]; /* 幂等记录数组 */
    int count;                              /* 已存储记录数 */
} IdempotencyOrderService;

/** 初始化幂等服务 */
void idempotency_service_init(IdempotencyOrderService *service);

/**
 * 创建订单（带幂等保护）。
 * 三条路径：首次→CREATED，重复+匹配→replayed，重复+不匹配→CONFLICT。
 */
void create_order_with_idempotency(
        IdempotencyOrderService *service,
        const char *idempotency_key,
        const char *order_id,
        const char *sku,
        int quantity,
        IdempotentOrderResponse *out_response
);

#endif
