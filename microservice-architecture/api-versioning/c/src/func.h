/**
 * @file func.h - API 版本管理模式（API Versioning Pattern）的 C 语言头文件
 *
 * 定义了版本路由的数据结构和函数接口。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：v1/v2 的响应逻辑是不同的策略，
 *     version_router_handle 根据解析结果选择对应策略执行。
 *   - 工厂方法模式（Factory Method）：版本解析逻辑根据 URL/Header 决定版本。
 *
 * 【架构思想】
 *   API 版本管理让新老客户端并行使用不同版本接口。
 *
 * 【开源对比】
 *   C 语言生态中版本路由通常在 Nginx 配置或 C 编写的网关中通过 URL 重写实现。
 *   本示例展示纯 C 实现的版本路由骨架。
 */

#ifndef API_VERSIONING_C_FUNC_H
#define API_VERSIONING_C_FUNC_H

typedef struct {
    char path[128];
    char api_version[16];
} VersionRequest;

typedef struct {
    int status_code;
    char version[16];
    char body[256];
} VersionResponse;

void version_router_handle(const VersionRequest *request, VersionResponse *response);

#endif
