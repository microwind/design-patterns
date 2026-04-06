#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    ConfigCenter center;
    config_center_init(&center);

    ServiceConfig v1 = {"order-service", "prod", 1, "db.prod.internal", 300, 0};
    config_center_put(&center, &v1);

    ConfigClient client;
    ServiceConfig loaded = {0};
    config_client_init(&client, &center, "order-service", "prod");
    assert(config_client_load(&client, &loaded) == 1);
    assert(loaded.version == 1);
    assert(loaded.timeout_ms == 300);

    ServiceConfig v2 = {"order-service", "prod", 2, "db.prod.internal", 500, 1};
    config_center_put(&center, &v2);

    ServiceConfig refreshed = {0};
    assert(config_client_refresh(&client, &refreshed) == 1);
    assert(refreshed.version == 2);
    assert(refreshed.feature_order_audit == 1);

    printf("configuration-center(c) tests passed\n");
    return 0;
}
