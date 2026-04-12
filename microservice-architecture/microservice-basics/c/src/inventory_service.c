/**
 * @file inventory_service.c - 本地库存服务实现（阶段1）
 *
 * 【设计模式】
 *   - 策略模式：reserve_impl 和 available_impl 作为函数指针的具体策略实现。
 *   - 适配器模式：将内存变量操作适配为统一的函数指针接口。
 *
 * 【架构思想】
 *   通过函数指针实现"接口与实现分离"，这是 C 语言中模拟面向对象
 *   依赖注入的经典手法。inventory_service_init 类似于构造函数，
 *   将具体实现绑定到结构体的函数指针上。
 *
 * 【开源对比】
 *   实际工程中库存数据存储在数据库，本示例用结构体字段模拟内存库存。
 */

#include "func.h"

/**
 * 预留库存的本地实现。
 * 通过 SKU 名称匹配对应的库存字段，检查并扣减。
 */
static int reserve_impl(InventoryService *service, const char *sku, int quantity)
{
    int *stock = NULL;

    /* 根据 SKU 匹配对应库存字段 */
    if (strcmp(sku, "SKU-BOOK") == 0) {
        stock = &service->book_stock;
    } else if (strcmp(sku, "SKU-PEN") == 0) {
        stock = &service->pen_stock;
    } else {
        return 0; /* 未知 SKU，拒绝 */
    }

    /* 库存不足，拒绝预留 */
    if (*stock < quantity) {
        return 0;
    }

    /* 扣减库存 */
    *stock -= quantity;
    return 1;
}

/**
 * 查询可用库存的本地实现。
 */
static int available_impl(InventoryService *service, const char *sku)
{
    if (strcmp(sku, "SKU-BOOK") == 0) {
        return service->book_stock;
    }
    if (strcmp(sku, "SKU-PEN") == 0) {
        return service->pen_stock;
    }
    return 0;
}

/**
 * 初始化本地库存服务。
 * 设置初始库存数据，绑定函数指针（类似构造函数 + 依赖注入）。
 */
void inventory_service_init(InventoryService *service)
{
    /* 初始化测试库存数据 */
    service->book_stock = 10;
    service->pen_stock = 1;
    /* 绑定函数指针（策略模式：绑定具体策略） */
    service->reserve = reserve_impl;
    service->available = available_impl;
}
