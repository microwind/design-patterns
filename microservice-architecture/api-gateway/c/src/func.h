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
