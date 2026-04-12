/**
 * @file order_service.c - 订单服务实现
 *
 * 【设计模式】
 *   - 依赖倒置原则（DIP）：通过 service->inventory 指针依赖库存服务，
 *     不直接调用具体函数，而是通过函数指针间接调用。
 *   - 外观模式（Facade）：create_order 封装了库存检查 + 订单创建的完整流程。
 *
 * 【架构思想】
 *   OrderService 只关心业务逻辑（检查库存、创建订单），不关心库存服务的实现细节。
 *   order_service_init 通过注入 inventory 指针实现解耦。
 *
 * 【开源对比】
 *   C 语言微服务通常通过 HTTP/gRPC 库（如 libcurl、grpc-c）实现服务间调用。
 *   本示例用函数指针模拟依赖注入的核心思想。
 */

#include "func.h"

/**
 * 创建订单的实现函数。
 * 先通过库存服务的函数指针调用 reserve，根据结果设置订单状态。
 */
static Order create_order_impl(OrderService *service, const char *order_id, const char *sku, int quantity)
{
    Order order;
    strcpy(order.order_id, order_id);
    strcpy(order.sku, sku);
    order.quantity = quantity;

    /* 通过函数指针调用库存服务（不关心是本地还是远程） */
    if (service->inventory->reserve(service->inventory, sku, quantity)) {
        strcpy(order.status, "CREATED");
    } else {
        strcpy(order.status, "REJECTED");
    }

    return order;
}

/**
 * 初始化订单服务，注入库存服务依赖。
 * 类似面向对象语言中的构造函数 + 依赖注入。
 */
void order_service_init(OrderService *service, InventoryService *inventory)
{
    service->inventory = inventory;
    /* 绑定创建订单的函数指针 */
    service->create_order = create_order_impl;
}
