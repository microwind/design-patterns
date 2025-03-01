package test.java.com.javaweborder.interfaces.routes;

import com.javaweborder.interfaces.controllers.OrderController;
import com.javaweborder.application.services.OrderService;
import com.javaweborder.infrastructure.repository.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderRoutesTest {

  private static final String API_PREFIX = "/api";
  private static final String BASE_URL = "http://localhost:8080";
  private static Thread serverThread;

  @BeforeAll
  public static void setUp() throws Exception {
    // 启动嵌入式 Tomcat 服务器
    serverThread = new Thread(() -> {
      TomcatServer.main(new String[]{});
    });
    serverThread.start();

    // 等待服务器启动
    Thread.sleep(5000);
  }

  @AfterAll
  public static void tearDown() throws Exception {
    // 停止服务器
    serverThread.interrupt();
  }

  @Test
  public void testOrderRoutes() throws IOException {
    // 初始化依赖
    OrderRepository orderRepository = new OrderRepository();
    OrderService orderService = new OrderService(orderRepository);
    OrderController orderController = new OrderController(orderService);

    // 初始化路由
    OrderRoutes.setupOrderRoutes(new Router(), orderController);

    // 测试创建订单
    String createOrderResponse = sendRequest(BASE_URL + API_PREFIX + "/orders", "POST",
            "{\"customerName\": \"齐天大圣\", \"amount\": 99.99}");
    System.out.println("创建订单测试结果：");
    System.out.println("响应体: " + createOrderResponse);

    assertEquals(201, getLastResponseCode(), "创建订单测试失败");

    // 获取订单ID
    String orderId = extractOrderId(createOrderResponse);

    // 测试获取订单
    String getOrderResponse = sendRequest(BASE_URL + API_PREFIX + "/orders/" + orderId, "GET", null);
    System.out.println("获取订单测试结果：");
    System.out.println("响应体: " + getOrderResponse);

    assertEquals(200, getLastResponseCode(), "获取订单测试失败");

    // 测试更新订单
    String updateOrderResponse = sendRequest(BASE_URL + API_PREFIX + "/orders/" + orderId, "PUT",
            "{\"customerName\": \"孙悟空\", \"amount\": 11.22}");
    System.out.println("更新订单测试结果：");
    System.out.println("响应体: " + updateOrderResponse);

    assertEquals(200, getLastResponseCode(), "更新订单测试失败");

    // 测试删除订单
    String deleteOrderResponse = sendRequest(BASE_URL + API_PREFIX + "/orders/" + orderId, "DELETE", null);
    System.out.println("删除订单测试结果：");
    System.out.println("响应体: " + deleteOrderResponse);

    assertEquals(204, getLastResponseCode(), "删除订单测试失败");

    System.out.println("所有测试通过！");
  }

  private String sendRequest(String urlString, String method, String body) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(method);
    connection.setRequestProperty("Content-Type", "application/json");

    if (body != null) {
      connection.setDoOutput(true);
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = body.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }
    }

    int responseCode = connection.getResponseCode();
    setLastResponseCode(responseCode);

    StringBuilder response = new StringBuilder();
    try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8)) {
      while (scanner.hasNextLine()) {
        response.append(scanner.nextLine());
      }
    }

    connection.disconnect();
    return response.toString();
  }

  private String extractOrderId(String jsonResponse) {
    // 简单的 JSON 解析，仅用于测试
    return jsonResponse.split("\"id\":")[1].split(",")[0].trim();
  }

  private static int lastResponseCode;

  private static void setLastResponseCode(int code) {
    lastResponseCode = code;
  }

  private static int getLastResponseCode() {
    return lastResponseCode;
  }
}