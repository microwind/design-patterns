// 接口层（Interfaces）：订单 Routes 设置
package com.javaweborder.interfaces.routes;

import com.javaweborder.interfaces.controllers.OrderController;

public class OrderRoutes {

    public static void setupOrderRoutes(Router router, OrderController orderController) {
        router.post("/orders", orderController::createOrder);
        router.get("/orders/{id}", orderController::getOrder);
        router.put("/orders/{id}", orderController::updateOrder);
        router.delete("/orders/{id}", orderController::deleteOrder);
    }
}


