/**
 * @file func.h - 分布式事务 Saga 模式的 C 语言头文件
 *
 * 【设计模式】
 *   - 命令模式：每个步骤（reserve/charge）和补偿（release）是独立函数。
 *   - 状态模式：订单状态 PENDING → COMPLETED / CANCELLED。
 *
 * 【架构思想】
 *   C 语言用结构体模拟服务，函数模拟正向步骤和补偿动作。
 */

#ifndef DISTRIBUTED_TRANSACTIONS_C_FUNC_H
#define DISTRIBUTED_TRANSACTIONS_C_FUNC_H

/** Saga 订单 */
typedef struct {
    char order_id[32];  /* 订单ID */
    char status[16];    /* PENDING / COMPLETED / CANCELLED */
} SagaOrder;

/** 库存服务 */
typedef struct {
    int book_stock;     /* 当前库存 */
} SagaInventoryService;

/** 支付服务 */
typedef struct {
    int fail;           /* 是否模拟支付失败（1=失败） */
} SagaPaymentService;

/** Saga 协调者 */
typedef struct {
    SagaInventoryService inventory;  /* 库存服务 */
    SagaPaymentService payment;      /* 支付服务 */
} SagaCoordinator;

/** 初始化 Saga 协调者 */
void saga_init(SagaCoordinator *coordinator, int stock, int payment_fails);

/** 执行 Saga 事务：库存预占 → 支付 → 成功/补偿 */
SagaOrder saga_execute(SagaCoordinator *coordinator, const char *order_id, const char *sku, int quantity);

#endif
