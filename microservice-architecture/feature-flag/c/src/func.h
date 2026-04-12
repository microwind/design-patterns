/**
 * @file func.h - 特性开关模式的 C 语言头文件
 *
 * 【设计模式】策略模式：不同开关配置代表不同发布策略。
 * 【架构思想】C 语言用结构体+数组模拟开关注册表。
 */

#ifndef FEATURE_FLAG_C_FUNC_H
#define FEATURE_FLAG_C_FUNC_H

/** 开关配置（简化版：仅支持单用户白名单） */
typedef struct {
    int default_enabled;   /* 默认是否启用 */
    char allow_user[32];   /* 白名单用户（简化为单用户） */
} FeatureFlag;

/** 开关注册记录 */
typedef struct {
    char name[32];         /* 开关名称 */
    FeatureFlag config;    /* 开关配置 */
} FeatureFlagRecord;

/** 特性开关服务 */
typedef struct {
    FeatureFlagRecord records[8]; /* 开关数组 */
    int count;                    /* 已注册数量 */
} FeatureFlagService;

/** 初始化特性开关服务 */
void feature_flag_service_init(FeatureFlagService *service);
/** 注册开关配置 */
void feature_flag_set(FeatureFlagService *service, const char *name, const FeatureFlag *config);
/** 评估开关是否对指定用户启用，返回 1=启用，0=禁用 */
int feature_flag_enabled(const FeatureFlagService *service, const char *name, const char *user_id);

#endif
