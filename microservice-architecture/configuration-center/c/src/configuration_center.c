/**
 * @file configuration_center.c - 配置中心模式（Configuration Center Pattern）的 C 语言实现
 *
 * 【设计模式】
 *   - 注册表模式：用数组线性查找实现 key-value 存储。
 *   - 代理模式：ConfigClient 函数代理 ConfigCenter 访问。
 *
 * 【架构思想】
 *   C 语言配置管理通常基于配置文件 + 信号/inotify 重载。
 *   本示例展示内存级配置中心的核心逻辑。
 *
 * 【开源对比】
 *   - Nginx：通过 nginx.conf 配置，支持 reload 信号重载
 *   - Envoy：通过 xDS API 动态更新配置
 */

#include "func.h"

#include <string.h>

/** 初始化配置中心 */
void config_center_init(ConfigCenter *center)
{
    center->count = 0;
}

/**
 * 发布配置。
 * 如果同 service_name + environment 的配置已存在，则覆盖（支持更新）；
 * 否则追加到数组末尾。
 */
void config_center_put(ConfigCenter *center, const ServiceConfig *config)
{
    /* 查找是否已存在相同 key 的配置 */
    for (int i = 0; i < center->count; i++) {
        if (strcmp(center->configs[i].service_name, config->service_name) == 0 &&
            strcmp(center->configs[i].environment, config->environment) == 0) {
            /* 覆盖更新 */
            center->configs[i] = *config;
            return;
        }
    }
    /* 追加新配置 */
    center->configs[center->count++] = *config;
}

/**
 * 获取指定服务和环境的配置。
 * @return 1=找到并写入 out_config，0=未找到
 */
int config_center_get(const ConfigCenter *center, const char *service_name, const char *environment, ServiceConfig *out_config)
{
    for (int i = 0; i < center->count; i++) {
        if (strcmp(center->configs[i].service_name, service_name) == 0 &&
            strcmp(center->configs[i].environment, environment) == 0) {
            *out_config = center->configs[i];
            return 1;
        }
    }
    return 0;
}

/** 初始化配置客户端，绑定特定服务和环境。 */
void config_client_init(ConfigClient *client, ConfigCenter *center, const char *service_name, const char *environment)
{
    client->center = center;
    strcpy(client->service_name, service_name);
    strcpy(client->environment, environment);
    client->loaded = 0;
}

/** 加载配置（从配置中心拉取并缓存）。 */
int config_client_load(ConfigClient *client, ServiceConfig *out_config)
{
    if (config_center_get(client->center, client->service_name, client->environment, &client->current)) {
        client->loaded = 1;
        *out_config = client->current;
        return 1;
    }
    return 0;
}

/** 刷新配置（重新从配置中心拉取）。 */
int config_client_refresh(ConfigClient *client, ServiceConfig *out_config)
{
    return config_client_load(client, out_config);
}
