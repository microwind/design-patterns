package com.github.microwind.userdemo;

import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import com.github.microwind.springwind.web.DispatcherServlet;

import java.io.File;

/**
 * 嵌入式Tomcat服务器
 */
public class TomcatServer {
    private Tomcat tomcat;
    private int port;

    public TomcatServer(int port) {
        this.port = port;
    }

    public void start(String configClass) throws LifecycleException {
        tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(new File("target/tomcat").getAbsolutePath());

        // 创建 Context
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);

        // 创建并注册 DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        String servletName = "dispatcherServlet";

        // Tomcat 11 写法：addServlet(Context, String, Servlet)
        Wrapper wrapper = Tomcat.addServlet(context, servletName, dispatcherServlet);

        // 设置初始化参数
        wrapper.addInitParameter("configClass", configClass);

        // 重要：设置 loadOnStartup，让 Servlet 在启动时初始化
        wrapper.setLoadOnStartup(1);

        // 映射所有请求
        context.addServletMappingDecoded("/*", servletName);

        // 启动 Tomcat
        tomcat.getConnector();
        tomcat.start();
        System.out.println("========================================");
        System.out.println("Tomcat服务器已启动");
        System.out.println("请访问: http://localhost:" + port);
        System.out.println("========================================");
        System.out.println("\n可访问的URL:");
        System.out.println("  - http://localhost:" + port + "/auth/login");
        System.out.println("  - http://localhost:" + port + "/student/detail");
        System.out.println("  - http://localhost:" + port + "/class/list");
        System.out.println("========================================\n");
        tomcat.getServer().await();
    }

    public void stop() throws LifecycleException {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    public static void main(String[] args) throws LifecycleException {
        TomcatServer server = new TomcatServer(8080);
        server.start("com.github.microwind.userdemo.UserDemoApplication");
    }
}
