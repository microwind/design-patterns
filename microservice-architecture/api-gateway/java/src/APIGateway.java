package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
