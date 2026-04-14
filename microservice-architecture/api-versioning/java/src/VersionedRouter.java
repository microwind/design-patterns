package src;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * VersionedRouter - API 版本管理模式的 Java 实现
 *
 * 本模块演示微服务架构中 API 版本管理的核心机制：支持 URL 路径版本（/v1/、/v2/）
 * 和 Header 版本（X-API-Version），并提供默认版本兜底。
 *
 * 【设计模式】
 *   - 策略模式（Strategy Pattern）：不同版本的处理器（v1/v2）是可互换的策略，
 *     路由器根据解析到的版本号选择对应的处理策略执行。
 *   - 工厂方法模式（Factory Method）：resolveVersion 根据请求上下文（URL/Header/默认值）
 *     决定使用哪个版本，类似于工厂方法根据输入创建不同产品。
 *   - 模板方法模式（Template Method）：handle 定义了"解析版本 → 查找处理器 → 执行"
 *     的固定骨架，具体版本解析和处理逻辑由子步骤决定。
 *
 * 【架构思想】
 *   API 版本管理让新老客户端可以并行使用不同版本的接口，实现平滑演进。
 *   URL 路径版本直观显式，Header 版本保持 URL 清洁，两者各有适用场景。
 *
 * 【开源对比】
 *   - Spring MVC：@RequestMapping + 自定义版本注解 / URL 路径参数
 *   - ASP.NET Core：Microsoft.AspNetCore.Mvc.Versioning（URL/Header/Query 多策略）
 *   - Kong / APISIX：通过路由规则和插件实现网关层版本路由
 *   本示例省略了版本协商、废弃通知、向后兼容等工程细节，聚焦于版本解析和路由分发。
 */
public class VersionedRouter {

    public static class Request {
        private final String path;
        private final Map<String, String> headers;

        public Request(String path, Map<String, String> headers) {
            this.path = path;
            this.headers = headers;
        }

        public String getPath() {
            return path;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }
    }

    public static class Response {
        private final int statusCode;
        private final String version;
        private final String body;

        public Response(int statusCode, String version, String body) {
            this.statusCode = statusCode;
            this.version = version;
            this.body = body;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getVersion() {
            return version;
        }

        public String getBody() {
            return body;
        }
    }

    private final String defaultVersion;
    private final Map<String, Supplier<String>> handlers = new HashMap<>();

    public VersionedRouter(String defaultVersion) {
        this.defaultVersion = normalize(defaultVersion);
    }

    public void register(String version, Supplier<String> handler) {
        handlers.put(normalize(version), handler);
    }

    public Response handle(Request request) {
        String version = resolveVersion(request);
        Supplier<String> handler = handlers.get(version);
        if (handler == null) {
            return new Response(400, version, "unsupported api version");
        }
        return new Response(200, version, handler.get());
    }

    public String resolveVersion(Request request) {
        String path = request.getPath().toLowerCase(Locale.ROOT);
        if (path.contains("/v2/")) {
            return "v2";
        }
        if (path.contains("/v1/")) {
            return "v1";
        }

        String headerVersion = normalize(request.getHeaders().getOrDefault("X-API-Version", ""));
        if (!headerVersion.isBlank()) {
            return headerVersion;
        }
        return defaultVersion;
    }

    public static String productHandlerV1() {
        return "{\"id\":\"P100\",\"name\":\"Mechanical Keyboard\"}";
    }

    public static String productHandlerV2() {
        return "{\"id\":\"P100\",\"name\":\"Mechanical Keyboard\",\"inventoryStatus\":\"IN_STOCK\"}";
    }

    private String normalize(String version) {
        String normalized = version.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank()) {
            return "";
        }
        return normalized.startsWith("v") ? normalized : "v" + normalized;
    }
}
