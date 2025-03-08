package com.javaweborder;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.connector.Connector;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TomcatServerTest {

    private final String helloResponseText = "Hello, World! 欢迎来到 Tomcat embedded server!";

    @Test
    public void testTomcatServerStartup() throws Exception {
        // 创建嵌入式 Tomcat 实例并指定端口
        Tomcat tomcat = new Tomcat();
        int port = 8080;
        tomcat.setPort(port);

        // 输出一些调试信息
        System.out.println("Initializing Tomcat server on port " + port + "...");

        // 获取 Tomcat 的 Connector 信息
        Connector connector = tomcat.getConnector();
        System.out.println("Tomcat connector details: " + connector);

        // 创建 Web 应用上下文
        String contextPath = "";
        String baseDir = new File("src/main/webapp").getAbsolutePath();
        System.out.println("Base directory for the web application: " + baseDir);

        // 创建和添加上下文
        Context context = tomcat.addContext(contextPath, baseDir);
        System.out.println("Web application context added at: " + context.getPath());

        // 添加一个简单的 Servlet
        Tomcat.addServlet(context, "HelloServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.setContentType("text/html; charset=UTF-8");
                resp.getWriter().write(helloResponseText);
            }
        });
        context.addServletMappingDecoded("/hello", "HelloServlet");
        System.out.println("Servlet '/hello' mapped successfully.");

        // 启动 Tomcat 服务器
        tomcat.start();
        System.out.println("Tomcat started successfully on port " + port + ".");

        // 验证 /hello 请求
        testHelloRequest(port);

        // 获取 Tomcat 服务器信息
        String serverInfo = tomcat.getServer().getStateName();
        System.out.println("Tomcat server info: " + serverInfo);

        // 关闭 Tomcat 服务器，在测试场景下避免一直阻塞
        tomcat.stop();
        tomcat.destroy();
    }

    private void testHelloRequest(int port) throws IOException {
        // 构建请求 URL
        URL url = new URL("http://localhost:" + port + "/hello");
        // 打开 HTTP 连接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 设置请求方法为 GET
        connection.setRequestMethod("GET");

        // 获取响应状态码
        int responseCode = connection.getResponseCode();
        // 验证响应状态码是否为 200
        assertEquals(200, responseCode, "响应状态码应为 200");

        // 读取响应内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // 验证响应内容是否符合预期
        assertEquals(helloResponseText, response.toString(), "响应内容不符合预期");
    }
}