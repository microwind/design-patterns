// 接口层（Interfaces）：订单 Routes 设置
package com.javaweborder.interfaces.routes;

import com.javaweborder.interfaces.controllers.OrderController;

public class OrderRoutes {

    public static void setupOrderRoutes(Router router, OrderController orderController) {
        // Java里面更流行的是通过注解将路由配置在Controller的方法里面，此处是为了说明routes的作用
        router.get("/api/orders/:id", orderController::getOrder);
        router.put("/api/orders/:id", orderController::updateOrder);
        router.delete("/api/orders/:id", orderController::deleteOrder);
        router.post("/api/orders", orderController::createOrder);
        router.get("/api/orders", orderController::listOrder);
        System.out.println("OrderRoutes->setupOrderRoutes done.");
    }
}


