/**
 * @file feature_flag.c - 特性开关模式的 C 语言实现
 *
 * 【设计模式】策略模式：不同开关配置代表不同发布策略。
 * 【架构思想】C 语言用数组线性查找实现开关注册表。
 * 【开源对比】实际工程中开关通常存储在远程平台，C 客户端定期拉取。
 */

#include "func.h"

#include <string.h>

/** 初始化特性开关服务 */
void feature_flag_service_init(FeatureFlagService *service)
{
    service->count = 0;
}

/** 注册开关配置 */
void feature_flag_set(FeatureFlagService *service, const char *name, const FeatureFlag *config)
{
    strcpy(service->records[service->count].name, name);
    service->records[service->count].config = *config;
    service->count++;
}

/**
 * 评估开关是否对指定用户启用。
 * 判断顺序：白名单优先 → 默认值兜底 → 未注册返回 0。
 */
int feature_flag_enabled(const FeatureFlagService *service, const char *name, const char *user_id)
{
    for (int i = 0; i < service->count; i++) {
        if (strcmp(service->records[i].name, name) == 0) {
            /* 白名单优先 */
            if (strcmp(service->records[i].config.allow_user, user_id) == 0) {
                return 1;
            }
            /* 兜底：返回默认值 */
            return service->records[i].config.default_enabled;
        }
    }
    /* 未注册的开关默认禁用 */
    return 0;
}
