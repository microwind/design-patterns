// 接口层（Interfaces）：订单 Routes 设置
package com.javaweborder.interfaces.routes;

import com.javaweborder.interfaces.controllers.OrderController;
import com.javaweborder.utils.LogUtils;

public class OrderRoutes {

    public static void setupOrderRoutes(Router router, OrderController orderController) {
        // 测试路由
        router.get("/api/hello", (req, resp) -> {
            resp.getWriter().write("Hello world!");
        });

        router.post("/api/orders", orderController::createOrder);
        router.get("/api/orders/{id}", orderController::getOrder);
        router.put("/api/orders/{id}", orderController::updateOrder);
        router.delete("/api/orders/{id}", orderController::deleteOrder);
        System.out.println("OrderRoutes setupOrderRoutes done.");
    }
}


