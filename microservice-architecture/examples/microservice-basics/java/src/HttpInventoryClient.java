package src;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class HttpInventoryClient implements InventoryClient {

    private final HttpClient httpClient;
    private final String baseUrl;

    public HttpInventoryClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean reserve(String sku, int quantity) {
        String encodedSku = URLEncoder.encode(sku, StandardCharsets.UTF_8);
        String path = "/reserve?sku=" + encodedSku + "&quantity=" + quantity;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 && "OK".equals(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        } catch (IOException ex) {
            return false;
        }
    }
}
