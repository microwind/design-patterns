package com.javaweborder.interfaces.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.javaweborder.interfaces.controllers.OrderController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;

// 导入 TomcatServer 类
import com.javaweborder.TomcatServer;

public class OrderRoutesTest {

  private static final String API_PREFIX = "/api";
  private static final String BASE_URL = "http://localhost:8080";
  private static Thread serverThread;
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeAll
  public static void setUp() throws Exception {
    // 启动嵌入式 Tomcat 服务器
    serverThread = new Thread(() -> {
      try {
        TomcatServer.main(new String[]{});
      } catch (Exception e) {
        System.err.println("启动服务器时出错: " + e.getMessage());
      }
    });
    serverThread.start();

    // 等待服务器启动
    Thread.sleep(5000);
  }

  @AfterAll
  public static void tearDown() throws Exception {
    // 停止服务器
    if (serverThread != null) {
      serverThread.interrupt();
    }
  }

  @Test
  public void testOrderRoutes() throws IOException {
    // 初始化依赖
    OrderController orderController = new OrderController();
    // 初始化路由
    OrderRoutes.setupOrderRoutes(new Router(), orderController);

    // 测试创建订单
    String createOrderResponse = sendRequest(BASE_URL + API_PREFIX + "/orders", "POST",
            "{\"customerName\": \"齐天大圣\", \"amount\": 99.99}");
    System.out.println("创建订单测试状态码："  + getLastResponseCode());
    System.out.println("响应体: " + createOrderResponse);

    assertEquals(201, getLastResponseCode(), "创建订单测试失败");

    // 获取订单ID
    String orderId = extractOrderId(createOrderResponse);

    // 测试获取订单
    String getOrderResponse = sendRequest(BASE_URL + API_PREFIX + "/orders/" + orderId, "GET", null);
    System.out.println("获取订单测试状态码："  + getLastResponseCode());
    System.out.println("响应体: " + getOrderResponse);

    assertEquals(200, getLastResponseCode(), "获取订单测试失败");

    // 测试更新订单
    String updateOrderResponse = sendRequest(BASE_URL + API_PREFIX + "/orders/" + orderId, "PUT",
            "{\"customerName\": \"孙悟空\", \"amount\": 11.22}");
    System.out.println("更新订单测试状态码："  + getLastResponseCode());
    System.out.println("响应体: " + updateOrderResponse);

    assertEquals(200, getLastResponseCode(), "更新订单测试失败");

    // 测试获取订单
    String getAllOrdersResponse = sendRequest(BASE_URL + API_PREFIX + "/orders", "GET", null);
    System.out.println("获取全部订单测试状态码："  + getLastResponseCode());
    System.out.println("响应体: " + getOrderResponse);

    assertEquals(200, getLastResponseCode(), "获取订单测试失败");

    // 测试删除订单
    String deleteOrderResponse = sendRequest(BASE_URL + API_PREFIX + "/orders/" + orderId, "DELETE", null);
    System.out.println("删除订单测试状态码："  + getLastResponseCode());
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
    try (Scanner scanner = new Scanner(connection.getInputStream(), String.valueOf(StandardCharsets.UTF_8))) {
      while (scanner.hasNextLine()) {
        response.append(scanner.nextLine());
      }
    } catch (IOException e) {
      // 处理获取响应体时的异常
      System.err.println("获取响应体时出错: " + e.getMessage());
      if (connection.getErrorStream() != null) {
        try (Scanner errorScanner = new Scanner(connection.getErrorStream(), String.valueOf(StandardCharsets.UTF_8))) {
          while (errorScanner.hasNextLine()) {
            response.append(errorScanner.nextLine());
          }
        }
      }
    }

    connection.disconnect();
    return response.toString();
  }

  private String extractOrderId(String jsonResponse) {
    try {
      // 使用 Jackson 解析 JSON
      JsonNode rootNode = objectMapper.readTree(jsonResponse);
      JsonNode dataNode = rootNode.get("data");
      // 检查是否存在 data 字段
      return dataNode.get("id").asText();
    } catch (IOException e) {
      System.err.println("解析订单 ID 时出错: " + e.getMessage());
      return null;
    }
  }

  private static int lastResponseCode;

  private static void setLastResponseCode(int code) {
    lastResponseCode = code;
  }

  private static int getLastResponseCode() {
    return lastResponseCode;
  }
}