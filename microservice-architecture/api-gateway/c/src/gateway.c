/**
 * @file gateway.c - API 网关模式（API Gateway Pattern）的 C 语言实现
 *
 * 实现路由匹配、鉴权中间件和 Correlation ID 注入。
 */

#include "func.h"

#include <stdio.h>
#include <string.h>

static void response_add_header(GatewayResponse *response, const char *key, const char *value)
{
    if (response->header_count >= MAX_HEADERS) {
        return;
    }
    strcpy(response->headers[response->header_count].key, key);
    strcpy(response->headers[response->header_count].value, value);
    response->header_count++;
}

void request_add_header(GatewayRequest *request, const char *key, const char *value)
{
    if (request->header_count >= MAX_HEADERS) {
        return;
    }
    strcpy(request->headers[request->header_count].key, key);
    strcpy(request->headers[request->header_count].value, value);
    request->header_count++;
}

const char *request_get_header(const GatewayRequest *request, const char *key)
{
    for (int i = 0; i < request->header_count; i++) {
        if (strcmp(request->headers[i].key, key) == 0) {
            return request->headers[i].value;
        }
    }
    return NULL;
}

void gateway_init(APIGateway *gateway)
{
    gateway->route_count = 0;
}

void gateway_register(APIGateway *gateway, const char *prefix, GatewayHandler handler)
{
    strcpy(gateway->routes[gateway->route_count].prefix, prefix);
    gateway->routes[gateway->route_count].handler = handler;
    gateway->route_count++;
}

GatewayResponse gateway_handle(const APIGateway *gateway, const GatewayRequest *request)
{
    GatewayHandler matched = NULL;
    int longest = -1;
    for (int i = 0; i < gateway->route_count; i++) {
        const char *prefix = gateway->routes[i].prefix;
        if (strncmp(request->path, prefix, strlen(prefix)) == 0 && (int)strlen(prefix) > longest) {
            longest = (int)strlen(prefix);
            matched = gateway->routes[i].handler;
        }
    }

    if (strncmp(request->path, "/api/orders", 11) == 0 && request_get_header(request, "X-User") == NULL) {
        GatewayResponse unauthorized = {0};
        unauthorized.status_code = 401;
        strcpy(unauthorized.body, "gateway: unauthorized");
        return unauthorized;
    }

    if (matched == NULL) {
        GatewayResponse not_found = {0};
        not_found.status_code = 404;
        strcpy(not_found.body, "gateway: route not found");
        return not_found;
    }

    GatewayResponse response = matched(request);
    const char *trace = request_get_header(request, "X-Correlation-ID");
    if (trace == NULL) {
        trace = "gw-generated-correlation-id";
    }
    response_add_header(&response, "X-Correlation-ID", trace);
    return response;
}

GatewayResponse order_service_handler(const GatewayRequest *request)
{
    GatewayResponse response = {0};
    response.status_code = 200;
    snprintf(response.body, sizeof(response.body), "order-service handled %s", request->path);
    response_add_header(&response, "X-Upstream-Service", "order-service");
    return response;
}

GatewayResponse inventory_service_handler(const GatewayRequest *request)
{
    GatewayResponse response = {0};
    response.status_code = 200;
    snprintf(response.body, sizeof(response.body), "inventory-service handled %s", request->path);
    response_add_header(&response, "X-Upstream-Service", "inventory-service");
    return response;
}
