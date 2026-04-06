#include "func.h"

#include <string.h>

void config_center_init(ConfigCenter *center)
{
    center->count = 0;
}

void config_center_put(ConfigCenter *center, const ServiceConfig *config)
{
    for (int i = 0; i < center->count; i++) {
        if (strcmp(center->configs[i].service_name, config->service_name) == 0 &&
            strcmp(center->configs[i].environment, config->environment) == 0) {
            center->configs[i] = *config;
            return;
        }
    }
    center->configs[center->count++] = *config;
}

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

void config_client_init(ConfigClient *client, ConfigCenter *center, const char *service_name, const char *environment)
{
    client->center = center;
    strcpy(client->service_name, service_name);
    strcpy(client->environment, environment);
    client->loaded = 0;
}

int config_client_load(ConfigClient *client, ServiceConfig *out_config)
{
    if (config_center_get(client->center, client->service_name, client->environment, &client->current)) {
        client->loaded = 1;
        *out_config = client->current;
        return 1;
    }
    return 0;
}

int config_client_refresh(ConfigClient *client, ServiceConfig *out_config)
{
    return config_client_load(client, out_config);
}
