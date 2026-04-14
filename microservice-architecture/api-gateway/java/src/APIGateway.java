package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APIGateway - API 网关模式的 Java 实现
 *
 * API 网关是微服务架构的统一入口，负责请求路由、中间件处理（鉴权/限流/日志）
 * 和跨切面关注点（如 Correlation ID 注入）。
 *
 * 【设计模式】
 *   - 外观模式（Facade Pattern）：网关为客户端提供统一入口，屏蔽后端多个微服务的
 *     拆分细节，客户端只需与网关交互。
 *   - 责任链模式（Chain of Responsibility）：中间件按注册顺序依次执行，任一中间件
 *     可拦截请求并直接返回响应（如鉴权失败返回 401），否则传递给下一环节。
 *   - 策略模式（Strategy Pattern）：Handler 和 Middleware 作为函数式接口，
 *     不同的路由处理器和中间件是可插拔的策略。
 *
 * 【架构思想】
 *   API 网关集中处理跨切面关注点（认证、限流、日志、链路追踪），使后端服务聚焦业务逻辑。
 *
 * 【开源对比】
 *   - Spring Cloud Gateway：基于 WebFlux 的反应式网关，支持路由谓词、过滤器链
 *   - Kong：基于 Nginx/OpenResty 的高性能网关，插件生态丰富
 *   - APISIX：基于 Nginx/OpenResty 的云原生网关
 *   本示例省略了异步 I/O、动态路由、限流熔断等工程细节，聚焦于路由匹配和中间件链。
 */
public class APIGateway {

    public interface Handler {
        Response handle(Request request);
    }

    public interface Middleware {
        Response apply(Request request);
    }

    public static class Request {
        private final String method;
        private final String path;
        private final Map<String, String> headers;

        public Request(String method, String path, Map<String, String> headers) {
            this.method = method;
            this.path = path;
            this.headers = headers;
        }

        public String getMethod() {
            return method;
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
        private final String body;
        private final Map<String, String> headers;

        public Response(int statusCode, String body, Map<String, String> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }
    }

    private final Map<String, Handler> routes = new HashMap<>();
    private final List<Middleware> middlewares = new ArrayList<>();

    public void use(Middleware middleware) {
        middlewares.add(middleware);
    }

    public void register(String prefix, Handler handler) {
        routes.put(prefix, handler);
    }

    public Response handle(Request request) {
        for (Middleware middleware : middlewares) {
            Response response = middleware.apply(request);
            if (response != null) {
                return response;
            }
        }

        Handler handler = match(request.getPath());
        if (handler == null) {
            return new Response(404, "gateway: route not found", new HashMap<>());
        }

        Response response = handler.handle(request);
        response.getHeaders().putIfAbsent(
                "X-Correlation-ID",
                request.getHeaders().getOrDefault("X-Correlation-ID", "gw-generated-correlation-id")
        );
        return response;
    }

    private Handler match(String path) {
        String matchedPrefix = "";
        Handler matched = null;

        for (Map.Entry<String, Handler> entry : routes.entrySet()) {
            String prefix = entry.getKey();
            if (path.startsWith(prefix) && prefix.length() > matchedPrefix.length()) {
                matchedPrefix = prefix;
                matched = entry.getValue();
            }
        }
        return matched;
    }

    public static Middleware requireUserHeader(String prefix, String headerName) {
        return request -> {
            if (!request.getPath().startsWith(prefix)) {
                return null;
            }
            if (!request.getHeaders().containsKey(headerName) || request.getHeaders().get(headerName).isBlank()) {
                return new Response(401, "gateway: unauthorized", new HashMap<>());
            }
            return null;
        };
    }

    public static Handler orderServiceHandler() {
        return request -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Upstream-Service", "order-service");
            return new Response(200, "order-service handled " + request.getPath(), headers);
        };
    }

    public static Handler inventoryServiceHandler() {
        return request -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Upstream-Service", "inventory-service");
            return new Response(200, "inventory-service handled " + request.getPath(), headers);
        };
    }
}
