/**
 * @file registry.c - 服务发现模式（Service Discovery Pattern）的 C 语言实现
 *
 * 【设计模式】
 *   - 注册表模式（Registry Pattern）：用数组维护实例列表。
 *   - 策略模式（Strategy Pattern）：registry_next 实现轮询选择。
 *
 * 【架构思想】
 *   C 语言中服务发现通常嵌入在网络框架中（如 Nginx upstream 模块）。
 *
 * 【开源对比】
 *   - Nginx：upstream 块配置后端列表，支持轮询/加权/IP哈希
 *   - Envoy（C++）：通过 xDS API 动态更新后端集群
 *   本示例用固定数组简化，省略了动态内存管理和线程安全。
 */

#include "func.h"

#include <string.h>

/** 初始化注册中心，清零实例计数和轮询索引。 */
void registry_init(ServiceRegistry *registry)
{
    registry->count = 0;
    registry->next_index = 0;
}

/** 注册服务实例，追加到实例数组末尾。 */
void registry_register(ServiceRegistry *registry, const char *instance_id, const char *address)
{
    strcpy(registry->instances[registry->count].instance_id, instance_id);
    strcpy(registry->instances[registry->count].address, address);
    registry->count++;
}

/**
 * 摘除服务实例。
 * 遍历数组找到匹配的 instance_id，前移后续元素实现删除。
 * @return 1=摘除成功，0=实例不存在
 */
int registry_deregister(ServiceRegistry *registry, const char *instance_id)
{
    for (int i = 0; i < registry->count; i++) {
        if (strcmp(registry->instances[i].instance_id, instance_id) == 0) {
            /* 前移后续元素，覆盖被删除的实例 */
            for (int j = i; j < registry->count - 1; j++) {
                registry->instances[j] = registry->instances[j + 1];
            }
            registry->count--;
            /* 调整轮询索引，防止越界 */
            if (registry->next_index >= registry->count) {
                registry->next_index = 0;
            }
            return 1;
        }
    }
    return 0;
}

/**
 * 获取下一个可用实例（轮询策略）。
 * 通过 next_index 取模实现循环轮询。
 * @return 实例指针，无实例时返回 NULL
 */
const ServiceInstance *registry_next(ServiceRegistry *registry)
{
    if (registry->count == 0) {
        return NULL;
    }
    /* 取模实现轮询 */
    const ServiceInstance *instance = &registry->instances[registry->next_index % registry->count];
    registry->next_index++;
    return instance;
}
