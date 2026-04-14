/**
 * @file func.h - API 网关模式（API Gateway Pattern）的 C 语言头文件
 *
 * 定义了网关的数据结构和函数接口。
 *
 * 【设计模式】
 *   - 外观模式（Facade Pattern）：gateway_handle 为客户端提供统一入口。
 *   - 责任链模式（Chain of Responsibility）：鉴权检查在路由匹配前执行，
 *     拦截未授权请求直接返回 401。
 *   - 策略模式（Strategy Pattern）：GatewayHandler 函数指针实现可插拔的
 *     路由处理策略。
 *
 * 【架构思想】
 *   API 网关集中处理认证、路由、链路追踪等跨切面关注点。
 *
 * 【开源对比】
 *   C 语言生态中网关通常基于 Nginx（C 编写）模块或 OpenResty（Lua + Nginx）实现。
 *   本示例展示纯 C 实现的网关骨架。
 */

#ifndef API_GATEWAY_C_FUNC_H
#define API_GATEWAY_C_FUNC_H

#define MAX_HEADERS 8
#define MAX_ROUTES 8

typedef struct {
    char key[64];
    char value[128];
} Header;

typedef struct {
    char method[16];
    char path[128];
    Header headers[MAX_HEADERS];
    int header_count;
} GatewayRequest;

typedef struct {
    int status_code;
    char body[256];
    Header headers[MAX_HEADERS];
    int header_count;
} GatewayResponse;

typedef GatewayResponse (*GatewayHandler)(const GatewayRequest *request);

typedef struct {
    char prefix[64];
    GatewayHandler handler;
} GatewayRoute;

typedef struct {
    GatewayRoute routes[MAX_ROUTES];
    int route_count;
} APIGateway;

void gateway_init(APIGateway *gateway);
void gateway_register(APIGateway *gateway, const char *prefix, GatewayHandler handler);
GatewayResponse gateway_handle(const APIGateway *gateway, const GatewayRequest *request);
GatewayResponse order_service_handler(const GatewayRequest *request);
GatewayResponse inventory_service_handler(const GatewayRequest *request);
void request_add_header(GatewayRequest *request, const char *key, const char *value);
const char *request_get_header(const GatewayRequest *request, const char *key);

#endif
