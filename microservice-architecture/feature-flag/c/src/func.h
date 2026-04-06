#ifndef FEATURE_FLAG_C_FUNC_H
#define FEATURE_FLAG_C_FUNC_H

typedef struct {
    int default_enabled;
    char allow_user[32];
} FeatureFlag;

typedef struct {
    char name[32];
    FeatureFlag config;
} FeatureFlagRecord;

typedef struct {
    FeatureFlagRecord records[8];
    int count;
} FeatureFlagService;

void feature_flag_service_init(FeatureFlagService *service);
void feature_flag_set(FeatureFlagService *service, const char *name, const FeatureFlag *config);
int feature_flag_enabled(const FeatureFlagService *service, const char *name, const char *user_id);

#endif
