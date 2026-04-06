package src;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

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
