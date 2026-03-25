#include "func.h"

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

int reserve_over_http(const char *host, int port, const char *sku, int quantity)
{
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd < 0) {
        return 0;
    }

    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons((unsigned short)port);
    if (inet_pton(AF_INET, host, &addr.sin_addr) <= 0) {
        close(fd);
        return 0;
    }

    if (connect(fd, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
        close(fd);
        return 0;
    }

    char request[256];
    snprintf(request, sizeof(request),
             "GET /reserve?sku=%s&quantity=%d HTTP/1.1\r\nHost: %s:%d\r\nConnection: close\r\n\r\n",
             sku, quantity, host, port);

    if (write(fd, request, strlen(request)) < 0) {
        close(fd);
        return 0;
    }

    char response[1024];
    ssize_t len = read(fd, response, sizeof(response) - 1);
    close(fd);

    if (len <= 0) {
        return 0;
    }

    response[len] = '\0';
    return strstr(response, " 200 ") != NULL && strstr(response, "\r\n\r\nOK") != NULL;
}

void create_order_over_http(const char *host, int port, const char *order_id, const char *sku, int quantity, Order *out_order)
{
    strcpy(out_order->order_id, order_id);
    strcpy(out_order->sku, sku);
    out_order->quantity = quantity;

    if (reserve_over_http(host, port, sku, quantity)) {
        strcpy(out_order->status, "CREATED");
    } else {
        strcpy(out_order->status, "REJECTED");
    }
}
