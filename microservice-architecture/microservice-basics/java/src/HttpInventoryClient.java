package src;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * HttpInventoryClient - HTTP 远程库存客户端（阶段2）
 *
 * 【设计模式】
 *   - 适配器模式（Adapter Pattern）：将 HTTP 远程调用适配为 InventoryClient 接口，
 *     调用方（OrderService）无需感知底层是 HTTP 通信。
 *   - 代理模式（Proxy Pattern）：作为远程库存服务的本地代理，
 *     封装了网络通信、序列化和错误处理细节。
 *
 * 【架构思想】
 *   这是微服务通信的核心组件。当库存服务独立部署后，订单服务通过 HTTP 客户端
 *   调用远程接口。这种模式体现了"远程调用不能当成本地函数调用"的核心认知：
 *   需要处理网络超时、连接失败、序列化等问题。
 *
 * 【开源对比】
 *   - Spring Cloud OpenFeign：声明式 HTTP 客户端，自动处理序列化和负载均衡
 *   - Retrofit（Java/Android）：类型安全的 HTTP 客户端
 *   - gRPC：基于 HTTP/2 + Protobuf 的高性能 RPC 框架
 *   本示例使用 Java 11+ 原生 HttpClient，展示最基础的 HTTP 通信。
 */
public class HttpInventoryClient implements InventoryClient {

    /** Java 原生 HTTP 客户端 */
    private final HttpClient httpClient;

    /** 库存服务的基础 URL（如 http://localhost:8080） */
    private final String baseUrl;

    /**
     * 构造 HTTP 库存客户端
     *
     * @param baseUrl 库存服务的基础 URL
     */
    public HttpInventoryClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = baseUrl;
    }

    /**
     * 通过 HTTP 远程调用库存服务进行库存预留。
     * 将 reserve 请求转换为 HTTP GET 请求发送到远程服务。
     *
     * @param sku      商品 SKU 编码
     * @param quantity 预留数量
     * @return true=预留成功（HTTP 200 + 响应体 "OK"），false=失败或网络异常
     */
    @Override
    public boolean reserve(String sku, int quantity) {
        // URL 编码 SKU 参数，防止特殊字符导致请求异常
        String encodedSku = URLEncoder.encode(sku, StandardCharsets.UTF_8);
        String path = "/reserve?sku=" + encodedSku + "&quantity=" + quantity;

        // 构建 HTTP GET 请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        try {
            // 发送同步请求并获取响应
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // 判断预留是否成功：HTTP 200 且响应体为 "OK"
            return response.statusCode() == 200 && "OK".equals(response.body());
        } catch (InterruptedException ex) {
            // 线程中断，恢复中断标志并返回失败
            Thread.currentThread().interrupt();
            return false;
        } catch (IOException ex) {
            // 网络异常（连接失败、超时等），返回失败
            return false;
        }
    }
}
