#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    FeatureFlagService service;
    FeatureFlag config = {0, "user-1"};
    feature_flag_service_init(&service);
    feature_flag_set(&service, "new-checkout", &config);
    assert(feature_flag_enabled(&service, "new-checkout", "user-1") == 1);
    assert(feature_flag_enabled(&service, "new-checkout", "user-2") == 0);
    printf("feature-flag(c) tests passed\n");
    return 0;
}
