package com.github.microwind.webdemo;

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

        // 创建Context
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);

        // 注册DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        String servletName = "dispatcherServlet";
        Wrapper wrapper = Tomcat.addServlet(context, servletName, dispatcherServlet);

        // 配置初始化参数
        wrapper.addInitParameter("configClass", configClass);

        // 重要：设置 loadOnStartup，让 Servlet 在启动时初始化
        wrapper.setLoadOnStartup(1);

        // 映射路径
        context.addServletMappingDecoded("/*", servletName);

        // 启动Tomcat
        tomcat.start();
        System.out.println("========================================");
        System.out.println("Tomcat服务器已启动");
        System.out.println("访问地址: http://localhost:" + port);
        System.out.println("========================================");
        System.out.println("\n可访问的URL:");
        System.out.println("  - http://localhost:" + port + "/home/index (首页)");
        System.out.println("  - http://localhost:" + port + "/product/list (产品中心)");
        System.out.println("  - http://localhost:" + port + "/news/list (新闻资讯)");
        System.out.println("  - http://localhost:" + port + "/admin/columns (栏目管理)");
        System.out.println("  - http://localhost:" + port + "/admin/createColumn (创建栏目)");
        System.out.println("  - http://localhost:" + port + "/admin/publishArticle (发布文章)");
        System.out.println("========================================\n");
        tomcat.getServer().await();
    }

    public void stop() throws LifecycleException {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
    }
}
