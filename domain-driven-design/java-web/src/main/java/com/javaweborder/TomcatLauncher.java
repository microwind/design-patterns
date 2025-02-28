package com.javaweborder;

import com.javaweborder.config.ServerConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.connector.Connector;
import java.io.File;

public class TomcatLauncher {
    public static void main(String[] args) {
        try {
            System.out.println("Initializing  server...");
            // 加载应用配置
            ServerConfig config = new ServerConfig();
            // 创建嵌入式 Tomcat 实例并指定端口
            Tomcat tomcat = new Tomcat();
            // 获取端口配置
            int port = config.getPort();
            tomcat.setPort(port);

            // 获取 Tomcat 的 Connector 信息
            Connector connector = tomcat.getConnector();
            System.out.println("Tomcat connector details: " + connector);

            // 创建 Web 应用上下文
            String contextPath = "";
            String baseDir = new File(".").getAbsolutePath();
            System.out.println("contextPath:" + contextPath + " baseDir:" + baseDir);
            Context context = tomcat.addWebapp(contextPath, baseDir);
            // 注册 ServletContextListener
            context.addApplicationListener(Application.class.getName());

            // 启动 Tomcat 服务器
            String  address = tomcat.getServer().getAddress().toLowerCase() + ":" + port;
            System.out.println("Starting server on " + address);
            tomcat.start();
            System.out.println("The Server has started. please visit " + address + contextPath);
            tomcat.getServer().await();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}