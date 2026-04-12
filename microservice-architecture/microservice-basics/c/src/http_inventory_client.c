/**
 * @file http_inventory_client.c - HTTP 远程库存客户端（阶段2）
 *
 * 【设计模式】
 *   - 适配器模式（Adapter Pattern）：将 HTTP socket 通信适配为与本地 reserve
 *     相同的函数签名（返回 int 表示成功/失败）。
 *   - 代理模式（Proxy Pattern）：作为远程库存服务的本地代理。
 *
 * 【架构思想】
 *   这是微服务 HTTP 通信的最底层实现：手动创建 TCP socket、构造 HTTP 请求、
 *   解析 HTTP 响应。实际工程中会使用 libcurl 等库简化网络编程。
 *   展示了"远程调用不能当成本地函数调用"的本质——需要处理连接、超时、协议解析。
 *
 * 【开源对比】
 *   - libcurl：C 语言最流行的 HTTP 客户端库
 *   - Envoy（C++）：Service Mesh sidecar 代理，处理服务间通信
 *   本示例使用 POSIX socket API，展示最底层的 HTTP 通信原理。
 */

#include "func.h"

#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

/**
 * 通过 HTTP GET 调用远程库存服务进行库存预留。
 *
 * @param host     库存服务主机地址
 * @param port     库存服务端口
 * @param sku      商品 SKU 编码
 * @param quantity 预留数量
 * @return 1=预留成功（HTTP 200），0=失败或网络异常
 */
int reserve_over_http(const char *host, int port, const char *sku, int quantity)
{
    /* 创建 TCP socket */
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd < 0) {
        return 0;
    }

    /* 设置服务器地址 */
    struct sockaddr_in addr;
    addr.sin_family = AF_INET;
    addr.sin_port = htons((unsigned short)port);
    if (inet_pton(AF_INET, host, &addr.sin_addr) <= 0) {
        close(fd);
        return 0;
    }

    /* 建立 TCP 连接 */
    if (connect(fd, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
        close(fd);
        return 0;
    }

    /* 手动构造 HTTP GET 请求报文 */
    char request[256];
    snprintf(request, sizeof(request),
             "GET /reserve?sku=%s&quantity=%d HTTP/1.1\r\nHost: %s:%d\r\nConnection: close\r\n\r\n",
             sku, quantity, host, port);

    /* 发送请求 */
    if (write(fd, request, strlen(request)) < 0) {
        close(fd);
        return 0;
    }

    /* 读取响应 */
    char response[1024];
    ssize_t len = read(fd, response, sizeof(response) - 1);
    close(fd);

    if (len <= 0) {
        return 0;
    }

    /* 简单判断 HTTP 状态码是否为 200 */
    response[len] = '\0';
    return strstr(response, " 200 ") != NULL;
}

/**
 * 通过 HTTP 远程调用创建订单。
 * 先调用 reserve_over_http 预留库存，根据结果设置订单状态。
 */
void create_order_over_http(const char *host, int port, const char *order_id, const char *sku, int quantity, Order *out_order)
{
    strcpy(out_order->order_id, order_id);
    strcpy(out_order->sku, sku);
    out_order->quantity = quantity;

    /* 通过 HTTP 调用远程库存服务 */
    if (reserve_over_http(host, port, sku, quantity)) {
        strcpy(out_order->status, "CREATED");
    } else {
        strcpy(out_order->status, "REJECTED");
    }
}
