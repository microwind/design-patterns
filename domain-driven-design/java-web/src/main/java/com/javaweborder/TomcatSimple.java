
package com.javaweborder;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.connector.Connector;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

// 简单的Tomcat 嵌入式服务器 例子
public class TomcatSimple {
    public static void main(String[] args) {
        try {
            // 创建嵌入式 Tomcat 实例并指定端口
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);

            // 输出一些调试信息
            System.out.println("Initializing Tomcat server on port 8080...");

            // 获取 Tomcat 的 Connector 信息
            Connector connector = tomcat.getConnector();
            System.out.println("Tomcat connector details: " + connector);

            // 创建 Web 应用上下文
            String contextPath = "";
            String baseDir = new File(".").getAbsolutePath();
            System.out.println("Base directory for the web application: " + baseDir);

            // 创建和添加上下文
            Context context = tomcat.addContext(contextPath, baseDir);
            System.out.println("Web application context added at: " + context.getPath());

            // 添加一个简单的 Servlet
            Tomcat.addServlet(context, "HelloServlet", new HttpServlet() {
                @Override
                protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                    resp.setContentType("text/html; charset=UTF-8");
                    resp.getWriter().write("Hello, World! 欢迎来到 Tomcat embedded server!");
                }
            });
            context.addServletMappingDecoded("/hello", "HelloServlet");
            System.out.println("Servlet '/hello' mapped successfully.");

            // 启动 Tomcat 服务器
            tomcat.start();
            System.out.println("Tomcat started successfully on port 7070.");

            // 获取 Tomcat 服务器信息
            String serverInfo = tomcat.getServer().getStateName();
            System.out.println("Tomcat server info: " + serverInfo);

            // 让 Tomcat 继续运行
            tomcat.getServer().await();
            System.out.println("Tomcat server is awaiting requests...");

        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}
