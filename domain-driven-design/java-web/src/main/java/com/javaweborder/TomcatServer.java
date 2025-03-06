package com.javaweborder;

import com.javaweborder.config.ServerConfig;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

public class TomcatServer {
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

            // 配置Connector编码
            Connector connector = tomcat.getConnector();
            connector.setURIEncoding("UTF-8");
            connector.setUseBodyEncodingForURI(true);

            // 创建Web上下文，创建webapp目录
            File webappDir = new File("src/main/webapp");
            if (!webappDir.exists()) webappDir.mkdirs();
            Context context = tomcat.addWebapp("", webappDir.getAbsolutePath());

            // 配置类加载资源，映射到WEB-INF/classes
            File classesDir = new File("target/classes");
            WebResourceRoot resources = new StandardRoot(context);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    classesDir.getAbsolutePath(), "/"));
            context.setResources(resources);

            // 显式启用注解扫描
            context.setAddWebinfClassesResources(true);

            // 注册监听器 ServletContextListener
            context.addApplicationListener(Application.class.getName());

            // 启动 Tomcat 服务器
            String  address = tomcat.getServer().getAddress().toLowerCase() + ":" + port;
            System.out.println("Starting server on " + address);
            tomcat.start();
            System.out.println("The Server has started. please visit " + address);
            tomcat.getServer().await();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}