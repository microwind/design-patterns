package com.javaweborder;

import com.javaweborder.config.ServerConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class TomcatLauncher {
    public static void main(String[] args) throws Exception {
        // 加载应用配置
        ServerConfig config = new ServerConfig();
        // 创建嵌入式 Tomcat 实例并指定端口
        Tomcat tomcat = new Tomcat();
        // 获取端口配置
        int port = config.getPort();
        tomcat.setPort(port);

        // 创建 Web 应用上下文
        String contextPath = "";
        String baseDir = new File(".").getAbsolutePath();
        Context context = tomcat.addWebapp(contextPath, baseDir);

        // 注册 ServletContextListener
        context.addApplicationListener(Application.class.getName());

        // 启动 Tomcat 服务器
        tomcat.start();
        tomcat.getServer().await();
    }
}