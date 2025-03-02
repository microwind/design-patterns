// 入口页面
package com.javaweborder;

import com.javaweborder.config.ServerConfig;
import com.javaweborder.infrastructure.configuration.LoggingConfig;
import com.javaweborder.infrastructure.message.MessageQueueService;
import com.javaweborder.interfaces.controllers.OrderController;
import com.javaweborder.interfaces.routes.OrderRoutes;
import com.javaweborder.interfaces.routes.Router;
import com.javaweborder.middleware.LoggingMiddleware;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebListener
public class Application implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // 加载应用配置
            ServerConfig config = new ServerConfig();
            int port = config.getPort(); // 从配置中获取端口

            // 初始化日志配置
            LoggingConfig loggingConfig = new LoggingConfig();
            loggingConfig.setLevel("INFO"); // 设置日志级别
            loggingConfig.setFile(config.getLogging().getFile()); // 设置日志文件路径
            loggingConfig.init();

            // 初始化消息队列服务，模拟消息服务
            MessageQueueService messageQueueService = new MessageQueueService();
            messageQueueService.receiveMessages(); // 启动消息接收线程

            // 创建 HTTP 控制器
            OrderController orderController = new OrderController();

            // 创建路由管理器
            Router router = new Router();

            // 设置订单路由
            OrderRoutes.setupOrderRoutes(router, orderController);

            // 获取 ServletContext 对象
            ServletContext context = sce.getServletContext();

            // 注册路由 Servlet
            context.addServlet("OrderRouter", router).addMapping("/api/*");
            // 测试路由
            router.get("/api/hello", (req, resp) -> {
                resp.getWriter().write("Hello world!");
            });

            // 注册首页 Servlet
            context.addServlet("HomeServlet", new HttpServlet() {
                @Override
                protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                    resp.setContentType("text/html; charset=UTF-8");
                    resp.getWriter().write(
                            "<h1>Welcome to DDD example.</h1>" +
                                    "<pre>" +
                                    "测试\n" +
                                    "<code>" +
                                    "创建：curl -X POST \"http://localhost:" + port + "/api/orders\" -H \"Content-Type: application/json; charset=UTF-8\" -d '{\"customerName\": \"齐天大圣\", \"amount\": 99.99}'\n" +
                                    "查询：curl -X GET \"http://localhost:" + port + "/api/orders/订单号\"\n" +
                                    "更新：curl -X PUT \"http://localhost:" + port + "/api/orders/订单号\" -H \"Content-Type: application/json; charset=UTF-8\" -d '{\"customerName\": \"孙悟空\", \"amount\": 11.22}'\n" +
                                    "删除：curl -X DELETE \"http://localhost:" + port + "/api/orders/订单号\"\n" +
                                    "查询：curl -X GET \"http://localhost:" + port + "/api/orders/订单号\"\n" +
                                    "</code>" +
                                    "详细：https://github.com/microwind/design-patterns/tree/main/domain-driven-design" +
                                    "</pre>"
                    );
                }
            }).addMapping("/");

            // 设置默认欢迎页面
            context.setAttribute("welcomeMessage", "<h1>Welcome to DDD example.</h1>");

            // 若无注解则需要手动注册 LoggingMiddleware 过滤器， 匹配所有请求。
            // FilterRegistration.Dynamic loggingFilter = context.addFilter("LoggingMiddleware", LoggingMiddleware.class);
            // loggingFilter.addMappingForUrlPatterns(null, false, "/*");

            System.out.println("Application context initialized. Java Servlet is running on port " + port);
        } catch (Exception e) {
            System.err.println("Application initialization failed." + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 清理资源
    }
}