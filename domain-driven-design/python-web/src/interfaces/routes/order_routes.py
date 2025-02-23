# -*- coding: utf-8 -*-
from src.interfaces.routes.router import create_router

api_prefix = "/api"

# 订单路由设置函数
def order_routes(order_controller, logging_middleware):
    router = create_router()

    router.post(
        api_prefix + '/orders',
        logging_middleware,
        order_controller.create_order
    )

    router.get(
        api_prefix + '/orders/:id',
        logging_middleware,
        order_controller.get_order
    )

    router.put(
        api_prefix + '/orders/:id',
        logging_middleware,
        order_controller.update_order
    )

    router.delete(
        api_prefix + '/orders/:id',
        logging_middleware,
        order_controller.delete_order
    )

    return router
