#include "func.h"

#include <string.h>

void feature_flag_service_init(FeatureFlagService *service)
{
    service->count = 0;
}

void feature_flag_set(FeatureFlagService *service, const char *name, const FeatureFlag *config)
{
    strcpy(service->records[service->count].name, name);
    service->records[service->count].config = *config;
    service->count++;
}

int feature_flag_enabled(const FeatureFlagService *service, const char *name, const char *user_id)
{
    for (int i = 0; i < service->count; i++) {
        if (strcmp(service->records[i].name, name) == 0) {
            if (strcmp(service->records[i].config.allow_user, user_id) == 0) {
                return 1;
            }
            return service->records[i].config.default_enabled;
        }
    }
    return 0;
}
