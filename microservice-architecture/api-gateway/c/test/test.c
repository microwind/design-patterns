#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

static const char *response_header(const GatewayResponse *response, const char *key)
{
    for (int i = 0; i < response->header_count; i++) {
        if (strcmp(response->headers[i].key, key) == 0) {
            return response->headers[i].value;
        }
    }
    return NULL;
}

int main(void)
{
    APIGateway gateway;
    gateway_init(&gateway);
    gateway_register(&gateway, "/api/orders", order_service_handler);
    gateway_register(&gateway, "/api/inventory", inventory_service_handler);

    GatewayRequest secured = {0};
    strcpy(secured.method, "GET");
    strcpy(secured.path, "/api/orders/ORD-1001");
    request_add_header(&secured, "X-User", "jarry");
    request_add_header(&secured, "X-Correlation-ID", "trace-1001");

    GatewayResponse secured_response = gateway_handle(&gateway, &secured);
    assert(secured_response.status_code == 200);
    assert(strcmp(response_header(&secured_response, "X-Correlation-ID"), "trace-1001") == 0);
    assert(strcmp(response_header(&secured_response, "X-Upstream-Service"), "order-service") == 0);

    GatewayRequest unauthorized = {0};
    strcpy(unauthorized.method, "GET");
    strcpy(unauthorized.path, "/api/orders/ORD-1002");
    GatewayResponse unauthorized_response = gateway_handle(&gateway, &unauthorized);
    assert(unauthorized_response.status_code == 401);

    GatewayRequest inventory = {0};
    strcpy(inventory.method, "GET");
    strcpy(inventory.path, "/api/inventory/SKU-BOOK");
    GatewayResponse inventory_response = gateway_handle(&gateway, &inventory);
    assert(inventory_response.status_code == 200);
    assert(strcmp(response_header(&inventory_response, "X-Upstream-Service"), "inventory-service") == 0);

    GatewayRequest missing = {0};
    strcpy(missing.method, "GET");
    strcpy(missing.path, "/api/unknown");
    GatewayResponse missing_response = gateway_handle(&gateway, &missing);
    assert(missing_response.status_code == 404);

    printf("api-gateway(c) tests passed\n");
    return 0;
}
