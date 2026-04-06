#ifndef CONFIGURATION_CENTER_C_FUNC_H
#define CONFIGURATION_CENTER_C_FUNC_H

typedef struct {
    char service_name[32];
    char environment[16];
    int version;
    char db_host[64];
    int timeout_ms;
    int feature_order_audit;
} ServiceConfig;

typedef struct {
    ServiceConfig configs[8];
    int count;
} ConfigCenter;

typedef struct {
    ConfigCenter *center;
    char service_name[32];
    char environment[16];
    ServiceConfig current;
    int loaded;
} ConfigClient;

void config_center_init(ConfigCenter *center);
void config_center_put(ConfigCenter *center, const ServiceConfig *config);
int config_center_get(const ConfigCenter *center, const char *service_name, const char *environment, ServiceConfig *out_config);
void config_client_init(ConfigClient *client, ConfigCenter *center, const char *service_name, const char *environment);
int config_client_load(ConfigClient *client, ServiceConfig *out_config);
int config_client_refresh(ConfigClient *client, ServiceConfig *out_config);

#endif
