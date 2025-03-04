# -*- coding: utf-8 -*-
import logging
from src.interfaces.routes.router import create_router
from src.middleware.logging_middleware import logging_middleware
from src.middleware.auth_middleware import auth_middleware

api_prefix = "/api"

# 订单路由设置函数
def order_routes(order_controller):
    if order_controller is None:
        raise ValueError("order_controller cannot be None")
    
    router = create_router()

    # 注册路由
    router.post(
        api_prefix + '/orders',
        logging_middleware,
        auth_middleware,
        order_controller.create_order
    )
    logging.debug("Registered route: POST /api/orders")

    router.get(
        api_prefix + '/orders/:id',
        logging_middleware,
        order_controller.get_order
    )
    logging.debug("Registered route: GET /api/orders/:id")

    router.get(
        api_prefix + '/orders',
        logging_middleware,
        order_controller.get_all_orders
    )
    logging.debug("Registered route: GET /api/orders")

    router.put(
        api_prefix + '/orders/:id',
        logging_middleware,
        auth_middleware,
        order_controller.update_order
    )
    logging.debug("Registered route: PUT /api/orders/:id")

    router.delete(
        api_prefix + '/orders/:id',
        logging_middleware,
        auth_middleware,
        order_controller.delete_order
    )
    logging.debug("Registered route: DELETE /api/orders/:id")

    return router
