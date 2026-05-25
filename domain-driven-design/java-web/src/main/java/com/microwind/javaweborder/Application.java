package com.microwind.javaweborder;

import com.microwind.javaweborder.application.services.OrderService;
import com.microwind.javaweborder.config.ServerConfig;
import com.microwind.javaweborder.domain.event.DomainEventPublisher;
import com.microwind.javaweborder.domain.order.OrderFactory;
import com.microwind.javaweborder.domain.repository.OrderRepository;
import com.microwind.javaweborder.domain.service.OrderPricingService;
import com.microwind.javaweborder.infrastructure.configuration.LoggingConfig;
import com.microwind.javaweborder.infrastructure.event.MessageQueueDomainEventPublisher;
import com.microwind.javaweborder.infrastructure.message.MessageQueueService;
import com.microwind.javaweborder.infrastructure.repository.OrderRepositoryImpl;
import com.microwind.javaweborder.interfaces.controllers.OrderController;
import com.microwind.javaweborder.interfaces.routes.OrderRoutes;
import com.microwind.javaweborder.interfaces.routes.Router;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 应用入口 & 组合根（Composition Root）。
 *
 * <p>本类同时承担两个职责：
 * <ol>
 *   <li><b>Servlet 生命周期钩子</b>：作为 {@link ServletContextListener}
 *       响应容器启动 / 销毁</li>
 *   <li><b>组合根</b>：把领域、应用、基础设施、接口四层的依赖一次性装配起来，
 *       再注入到接口层</li>
 * </ol>
 *
 * <h3>为什么需要组合根</h3>
 * 把依赖装配集中在"组合根"是手工依赖注入的最佳实践：
 * <ul>
 *   <li>各层不互相 {@code new} 出对方实例，便于替换实现
 *       （如换数据库、换消息中间件）</li>
 *   <li>测试时可以注入 Mock，无需改业务代码</li>
 *   <li>真实工程里这个角色通常由 Spring / Guice 等 IoC 容器自动接管</li>
 * </ul>
 *
 * <h3>装配顺序（自底向上）</h3>
 * <pre>
 *   基础设施层 → 领域服务 → 应用服务 → 接口层 → Servlet 容器
 * </pre>
 */
@WebListener
public class Application implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // 1. 加载配置
            ServerConfig config = new ServerConfig();
            int port = config.getPort();

            // 2. 初始化日志
            LoggingConfig loggingConfig = new LoggingConfig();
            loggingConfig.setLevel("INFO");
            loggingConfig.setFile(config.getLogging().getFile());
            loggingConfig.init();

            // 3. 装配基础设施层（仓储 + 事件发布器）
            MessageQueueService messageQueueService = new MessageQueueService();
            messageQueueService.receiveMessages();
            DomainEventPublisher eventPublisher =
                    new MessageQueueDomainEventPublisher(messageQueueService);
            OrderRepository orderRepository = new OrderRepositoryImpl();

            // 4. 装配领域层组件（工厂 / 领域服务）
            OrderFactory orderFactory = new OrderFactory();
            OrderPricingService pricingService = new OrderPricingService();

            // 5. 装配应用服务（构造器注入所有依赖）
            OrderService orderService = new OrderService(
                    orderRepository,
                    orderFactory,
                    pricingService,
                    eventPublisher
            );

            // 6. 装配接口层
            OrderController orderController = new OrderController(orderService);
            Router router = new Router();
            OrderRoutes.setupOrderRoutes(router, orderController);

            // 7. 注册到 Servlet 容器
            ServletContext context = sce.getServletContext();
            context.addServlet("OrderRouter", router).addMapping("/api/*");

            // 测试路由
            router.get("/api/hello", (req, resp) -> resp.getWriter().write("Hello world!"));

            // 首页 Servlet
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
                                    "查询全部：curl -X GET \"http://localhost:" + port + "/api/orders\"\n" +
                                    "</code>" +
                                    "详细：https://github.com/microwind/design-patterns/tree/main/domain-driven-design" +
                                    "</pre>"
                    );
                }
            }).addMapping("/");

            context.setAttribute("welcomeMessage", "<h1>Welcome to DDD example.</h1>");

            System.out.println("Application context initialized. Java Servlet is running on port "
                    + port + " in " + config.getEnv() + ".");
        } catch (Exception e) {
            System.err.println("Application initialization failed." + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 清理资源
    }
}
