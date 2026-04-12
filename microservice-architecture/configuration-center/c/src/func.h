/**
 * @file func.h - 配置中心模式（Configuration Center Pattern）的 C 语言头文件
 *
 * 【设计模式】
 *   - 注册表模式：ConfigCenter 按 service_name + environment 键存储配置。
 *   - 代理模式：ConfigClient 代理 ConfigCenter 访问并缓存配置。
 *
 * 【架构思想】
 *   C 语言用固定数组模拟配置存储，结构体模拟配置对象。
 *
 * 【开源对比】
 *   C 语言中配置管理通常通过配置文件（INI / YAML / JSON）+ 文件监听实现。
 *   本示例展示内存级配置中心的核心逻辑。
 */

#ifndef CONFIGURATION_CENTER_C_FUNC_H
#define CONFIGURATION_CENTER_C_FUNC_H

/** ServiceConfig - 服务配置结构体 */
typedef struct {
    char service_name[32];     /* 服务名称 */
    char environment[16];      /* 环境标识 */
    int version;               /* 配置版本号 */
    char db_host[64];          /* 数据库地址 */
    int timeout_ms;            /* 超时时间（毫秒） */
    int feature_order_audit;   /* 订单审计功能开关（1=开，0=关） */
} ServiceConfig;

/** ConfigCenter - 配置中心服务端 */
typedef struct {
    ServiceConfig configs[8];  /* 配置数组 */
    int count;                 /* 已存储配置数量 */
} ConfigCenter;

/** ConfigClient - 配置客户端 */
typedef struct {
    ConfigCenter *center;      /* 关联的配置中心 */
    char service_name[32];     /* 绑定的服务名 */
    char environment[16];      /* 绑定的环境 */
    ServiceConfig current;     /* 当前缓存的配置快照 */
    int loaded;                /* 是否已加载（1=是，0=否） */
} ConfigClient;

/** 初始化配置中心 */
void config_center_init(ConfigCenter *center);
/** 发布配置（同 key 覆盖） */
void config_center_put(ConfigCenter *center, const ServiceConfig *config);
/** 获取配置，返回 1=找到，0=未找到 */
int config_center_get(const ConfigCenter *center, const char *service_name, const char *environment, ServiceConfig *out_config);
/** 初始化配置客户端 */
void config_client_init(ConfigClient *client, ConfigCenter *center, const char *service_name, const char *environment);
/** 加载配置，返回 1=成功 */
int config_client_load(ConfigClient *client, ServiceConfig *out_config);
/** 刷新配置，返回 1=成功 */
int config_client_refresh(ConfigClient *client, ServiceConfig *out_config);

#endif
