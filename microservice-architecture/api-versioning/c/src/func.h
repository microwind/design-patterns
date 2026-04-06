#ifndef API_VERSIONING_C_FUNC_H
#define API_VERSIONING_C_FUNC_H

typedef struct {
    char path[128];
    char api_version[16];
} VersionRequest;

typedef struct {
    int status_code;
    char version[16];
    char body[256];
} VersionResponse;

void version_router_handle(const VersionRequest *request, VersionResponse *response);

#endif
