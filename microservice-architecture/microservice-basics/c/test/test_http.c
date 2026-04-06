#include "../src/func.h"

#include <arpa/inet.h>
#include <assert.h>
#include <netinet/in.h>
#include <pthread.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

static int g_stock = 2;

static void *inventory_server_once(void *arg)
{
    int server_fd = *(int *)arg;

    for (int i = 0; i < 2; i++) {
        int client_fd = accept(server_fd, NULL, NULL);
        if (client_fd < 0) {
            continue;
        }

        char buf[1024];
        ssize_t n = read(client_fd, buf, sizeof(buf) - 1);
        if (n <= 0) {
            close(client_fd);
            continue;
        }
        buf[n] = '\0';

        int quantity = 0;
        char *q = strstr(buf, "quantity=");
        if (q != NULL) {
            quantity = atoi(q + 9);
        }

        const char *response_ok = "HTTP/1.1 200 OK\r\nContent-Length: 2\r\nConnection: close\r\n\r\nOK";
        const char *response_fail = "HTTP/1.1 409 Conflict\r\nContent-Length: 8\r\nConnection: close\r\n\r\nNO_STOCK";

        if (strstr(buf, "GET /reserve?sku=SKU-BOOK") != NULL && quantity > 0 && g_stock >= quantity) {
            g_stock -= quantity;
            write(client_fd, response_ok, strlen(response_ok));
        } else {
            write(client_fd, response_fail, strlen(response_fail));
        }

        close(client_fd);
    }

    close(server_fd);
    return NULL;
}

int main(void)
{
    int server_fd = socket(AF_INET, SOCK_STREAM, 0);
    int opt = 1;
    setsockopt(server_fd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt));

    struct sockaddr_in addr;
    socklen_t addr_len = sizeof(addr);
    addr.sin_family = AF_INET;
    addr.sin_port = htons(0);
    addr.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    bind(server_fd, (struct sockaddr *)&addr, sizeof(addr));
    listen(server_fd, 2);
    getsockname(server_fd, (struct sockaddr *)&addr, &addr_len);

    int port = ntohs(addr.sin_port);
    pthread_t tid;

    pthread_create(&tid, NULL, inventory_server_once, (void *)&server_fd);

    Order success;
    create_order_over_http("127.0.0.1", port, "ORD-2001", "SKU-BOOK", 1, &success);
    assert(strcmp(success.status, "CREATED") == 0);

    Order failed;
    create_order_over_http("127.0.0.1", port, "ORD-2002", "SKU-BOOK", 2, &failed);
    assert(strcmp(failed.status, "REJECTED") == 0);

    pthread_join(tid, NULL);
    printf("microservice-basics(c/http) tests passed\n");
    return 0;
}
