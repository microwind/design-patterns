#include "../src/func.h"

#include <assert.h>
#include <stdio.h>
#include <string.h>

int main(void)
{
    VersionRequest default_request = {"/products/P100", ""};
    VersionResponse default_response = {0};
    version_router_handle(&default_request, &default_response);
    assert(strcmp(default_response.version, "v1") == 0);

    VersionRequest header_request = {"/products/P100", "2"};
    VersionResponse header_response = {0};
    version_router_handle(&header_request, &header_response);
    assert(strcmp(header_response.version, "v2") == 0);

    VersionRequest bad_request = {"/products/P100", "v9"};
    VersionResponse bad_response = {0};
    version_router_handle(&bad_request, &bad_response);
    assert(bad_response.status_code == 400);

    printf("api-versioning(c) tests passed\n");
    return 0;
}
