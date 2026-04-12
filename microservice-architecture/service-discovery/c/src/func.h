/**
 * @file func.h - 服务发现模式（Service Discovery Pattern）的 C 语言头文件
 *
 * 【设计模式】
 *   - 注册表模式（Registry Pattern）：ServiceRegistry 结构体维护实例数组。
 *   - 策略模式（Strategy Pattern）：registry_next 实现轮询选择策略。
 *
 * 【架构思想】
 *   C 语言用数组 + 索引模拟注册中心，MAX_INSTANCES 限制了最大实例数。
 *   实际工程中 Envoy / Nginx 的 upstream 模块用类似结构管理后端实例。
 */

#ifndef SERVICE_DISCOVERY_C_FUNC_H
#define SERVICE_DISCOVERY_C_FUNC_H

/** 最大注册实例数量 */
#define MAX_INSTANCES 8

/** ServiceInstance - 服务实例 */
typedef struct {
    char instance_id[32];  /* 实例唯一标识 */
    char address[64];      /* 实例网络地址 */
} ServiceInstance;

/**
 * ServiceRegistry - 服务注册中心
 * 使用固定大小数组存储实例，next_index 实现轮询。
 */
typedef struct {
    ServiceInstance instances[MAX_INSTANCES]; /* 实例数组 */
    int count;       /* 当前注册实例数量 */
    int next_index;  /* 轮询偏移量 */
} ServiceRegistry;

/** 初始化注册中心 */
void registry_init(ServiceRegistry *registry);
/** 注册服务实例 */
void registry_register(ServiceRegistry *registry, const char *instance_id, const char *address);
/** 摘除服务实例，返回 1=成功，0=不存在 */
int registry_deregister(ServiceRegistry *registry, const char *instance_id);
/** 获取下一个可用实例（轮询），无实例时返回 NULL */
const ServiceInstance *registry_next(ServiceRegistry *registry);

#endif
