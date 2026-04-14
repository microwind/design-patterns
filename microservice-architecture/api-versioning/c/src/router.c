/**
 * @file router.c - API 版本管理模式（API Versioning Pattern）的 C 语言实现
 *
 * 实现 URL 路径版本解析和 Header 版本回退逻辑。
 */

#include "func.h"

#include <stdio.h>
#include <string.h>

static const char *normalize_version(const char *version)
{
    if (version == NULL || version[0] == '\0') {
        return "";
    }
    return version[0] == 'v' ? version : (strcmp(version, "2") == 0 ? "v2" : "v1");
}

void version_router_handle(const VersionRequest *request, VersionResponse *response)
{
    const char *version = "";
    if (strstr(request->path, "/v2/") != NULL) {
        version = "v2";
    } else if (strstr(request->path, "/v1/") != NULL) {
        version = "v1";
    } else if (request->api_version[0] != '\0') {
        version = normalize_version(request->api_version);
    } else {
        version = "v1";
    }

    strcpy(response->version, version);
    if (strcmp(version, "v1") == 0) {
        response->status_code = 200;
        strcpy(response->body, "{\"id\":\"P100\",\"name\":\"Mechanical Keyboard\"}");
    } else if (strcmp(version, "v2") == 0) {
        response->status_code = 200;
        strcpy(response->body, "{\"id\":\"P100\",\"name\":\"Mechanical Keyboard\",\"inventoryStatus\":\"IN_STOCK\"}");
    } else {
        response->status_code = 400;
        strcpy(response->body, "unsupported api version");
    }
}
