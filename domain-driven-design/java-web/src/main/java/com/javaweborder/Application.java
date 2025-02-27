// 入口页面
package com.javaweborder;

import com.javaweborder.config.ServerConfig;
import com.javaweborder.infrastructure.configuration.DatabaseConfig;
import com.javaweborder.infrastructure.configuration.LoggingConfig;
import com.javaweborder.infrastructure.repository.OrderRepositoryImpl;
import com.javaweborder.application.services.OrderService;
import com.javaweborder.interfaces.controllers.OrderController;
import com.javaweborder.interfaces.routes.OrderRoutes;
import com.javaweborder.interfaces.routes.Router;
import com.javaweborder.utils.LogUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;

@WebListener
public class Application implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // 加载应用配置
//            ServerConfig config = new ServerConfig();
//            DatabaseConfig dbConfig = config.getDatabase();
//            LoggingConfig loggingConfig = config.getLogging();

            // 创建订单仓储实现
//            OrderRepositoryImpl orderRepository = new OrderRepositoryImpl();

            // 创建订单应用服务
//            OrderService orderService = new OrderService(orderRepository);

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

            // 设置默认欢迎页面
            context.setAttribute("welcomeMessage", "<h1>Welcome to DDD example.</h1>");
        } catch (Exception e) {
            LogUtils.logError("Application initialization failed.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 清理资源
    }
}

