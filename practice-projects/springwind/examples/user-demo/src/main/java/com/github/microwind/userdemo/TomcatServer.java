package com.github.microwind.userdemo;

import com.github.microwind.springwind.SpringWindApplication;
import com.github.microwind.springwind.core.SpringWindApplicationContext;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import com.github.microwind.springwind.web.DispatcherServlet;

import java.io.File;

/**
 * 嵌入式Tomcat服务器（应用层实现）
 */
public class TomcatServer {
    private Tomcat tomcat;
    private final int port;

    public TomcatServer(int port) {
        this.port = port;
    }

    public void start(SpringWindApplication app) throws Exception {
        tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(new File("target/tomcat").getAbsolutePath());

        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context tomcatContext = tomcat.addContext(contextPath, docBase);

        // 注入SpringWindApplicationContext的DispatcherServlet
        DispatcherServlet dispatcherServlet = new DispatcherServlet(app.getContext());
        String servletName = "dispatcherServlet";
        Wrapper wrapper = Tomcat.addServlet(tomcatContext, servletName, dispatcherServlet);
        wrapper.setLoadOnStartup(1);
        tomcatContext.addServletMappingDecoded("/*", servletName);

        tomcat.getConnector();
        tomcat.start();
        System.out.println("========================================");
        System.out.println("Tomcat服务器已启动");
        System.out.println("请访问: http://localhost:" + port);
        System.out.println("========================================");
        tomcat.getServer().await();
    }

    public void stop() throws LifecycleException {
        if (tomcat != null) {
            tomcat.stop();
            tomcat.destroy();
        }
    }
}
