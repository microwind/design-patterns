/**
 * @file saga.c - 分布式事务 Saga 模式的 C 语言实现
 *
 * 【设计模式】
 *   - 命令模式：reserve_stock / release_stock / charge 是独立的命令函数。
 *   - 责任链模式：saga_execute 按序调用步骤，失败时触发补偿。
 *   - 状态模式：订单状态通过 strcpy 在 PENDING / COMPLETED / CANCELLED 之间切换。
 *
 * 【架构思想】
 *   C 语言中 Saga 通常嵌入在业务流程代码中，用 if-else 链式编排。
 *
 * 【开源对比】
 *   实际工程中 Saga 协调者通常是独立的服务或工作流引擎。
 *   本示例用函数调用模拟编排式 Saga。
 */

#include "func.h"

#include <string.h>

/**
 * 正向步骤：预占库存。
 * @return 1=成功，0=失败
 */
static int reserve_stock(SagaInventoryService *inventory, const char *sku, int quantity)
{
    if (strcmp(sku, "SKU-BOOK") != 0 || quantity <= 0 || inventory->book_stock < quantity) {
        return 0;
    }
    inventory->book_stock -= quantity;
    return 1;
}

/**
 * 补偿动作：释放已预占的库存。
 */
static void release_stock(SagaInventoryService *inventory, const char *sku, int quantity)
{
    if (strcmp(sku, "SKU-BOOK") == 0 && quantity > 0) {
        inventory->book_stock += quantity;
    }
}

/** 初始化 Saga 协调者。 */
void saga_init(SagaCoordinator *coordinator, int stock, int payment_fails)
{
    coordinator->inventory.book_stock = stock;
    coordinator->payment.fail = payment_fails;
}

/**
 * 执行 Saga 事务。
 *
 * 流程：
 *   1. 库存预占 → 失败则 CANCELLED
 *   2. 支付扣款 → 失败则补偿释放库存 → CANCELLED
 *   3. 全部成功 → COMPLETED
 */
SagaOrder saga_execute(SagaCoordinator *coordinator, const char *order_id, const char *sku, int quantity)
{
    SagaOrder order = {0};
    strcpy(order.order_id, order_id);
    strcpy(order.status, "PENDING");

    /* 步骤1：库存预占 */
    if (!reserve_stock(&coordinator->inventory, sku, quantity)) {
        strcpy(order.status, "CANCELLED");
        return order;
    }

    /* 步骤2：支付扣款 */
    if (coordinator->payment.fail) {
        /* 支付失败 → 补偿：释放库存 */
        release_stock(&coordinator->inventory, sku, quantity);
        strcpy(order.status, "CANCELLED");
        return order;
    }

    /* 全部成功 */
    strcpy(order.status, "COMPLETED");
    return order;
}
