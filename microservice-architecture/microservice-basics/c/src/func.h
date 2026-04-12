/**
 * @file func.h - 微服务基础（Microservice Basics）的 C 语言头文件
 *
 * 定义了库存服务、订单服务和订单实体的数据结构与函数接口。
 *
 * 【设计模式】
 *   - 依赖倒置原则（DIP）：通过函数指针实现接口抽象，OrderService 依赖
 *     InventoryService 的函数指针而非具体函数名。
 *   - 策略模式（Strategy Pattern）：reserve 函数指针可指向本地实现或 HTTP 实现。
 *   - 适配器模式（Adapter Pattern）：reserve_over_http 将 HTTP 调用适配为
 *     与本地 reserve 相同的调用签名。
 *
 * 【架构思想】
 *   C 语言没有类和接口，通过结构体 + 函数指针模拟面向对象的依赖注入。
 *   这种模式在 Linux 内核（VFS 文件系统接口）和 Nginx（模块系统）中广泛使用。
 *
 * 【开源对比】
 *   - Nginx：通过函数指针表实现模块化，upstream 模块实现了负载均衡和健康检查
 *   - Envoy（C++）：通过虚函数实现服务间通信抽象
 */

#ifndef MICROSERVICE_BASICS_FUNC_H
#define MICROSERVICE_BASICS_FUNC_H

#include <stdio.h>
#include <string.h>

/* 前向声明 */
typedef struct InventoryService InventoryService;
typedef struct OrderService OrderService;
typedef struct Order Order;

/**
 * InventoryService - 库存服务结构体
 *
 * 通过函数指针（reserve / available）实现接口抽象，
 * 类似于面向对象语言中的接口 + 实现类。
 */
struct InventoryService {
    int book_stock;  /* SKU-BOOK 库存数量 */
    int pen_stock;   /* SKU-PEN 库存数量 */
    /** 预留库存函数指针（返回 1=成功，0=失败） */
    int (*reserve)(InventoryService *service, const char *sku, int quantity);
    /** 查询可用库存函数指针 */
    int (*available)(InventoryService *service, const char *sku);
};

/**
 * Order - 订单实体（值对象）
 */
struct Order {
    char order_id[32];  /* 订单ID */
    char sku[32];       /* 商品SKU */
    int quantity;       /* 订购数量 */
    char status[16];    /* 订单状态：CREATED / REJECTED */
};

/**
 * OrderService - 订单服务结构体
 *
 * 通过 inventory 指针依赖库存服务（依赖倒置），
 * create_order 函数指针封装了订单创建逻辑。
 */
struct OrderService {
    InventoryService *inventory;  /* 库存服务指针（依赖注入） */
    /** 创建订单函数指针 */
    Order (*create_order)(OrderService *service, const char *order_id, const char *sku, int quantity);
};

/** 初始化本地库存服务（阶段1） */
void inventory_service_init(InventoryService *service);

/** 初始化订单服务，注入库存服务依赖 */
void order_service_init(OrderService *service, InventoryService *inventory);

/** 通过 HTTP 调用远程库存服务进行库存预留（阶段2） */
int reserve_over_http(const char *host, int port, const char *sku, int quantity);

/** 通过 HTTP 远程调用创建订单（阶段2） */
void create_order_over_http(const char *host, int port, const char *order_id, const char *sku, int quantity, Order *out_order);

#endif
