// 接口层（Interfaces）：订单路由设置
// src/interfaces/routes/order-routes.js

import { createRouter } from './router.js';

export default function orderRoutes(orderController, loggingMiddleware) {
  const router = createRouter();

  // 创建订单
  router.post(
    '/orders',
    loggingMiddleware, // 添加日志中间件
    (req, res) => orderController.createOrder(req, res)
  );

  // 获取订单
  router.get(
    '/orders/:id',
    loggingMiddleware, // 添加日志中间件
    (req, res, query) => orderController.getOrder(req, res, query)
  );

  // 更新订单
  router.put(
    '/orders/:id',
    loggingMiddleware, // 添加日志中间件
    (req, res, query) => orderController.updateOrder(req, res, query)
  );

  // 删除订单
  router.delete(
    '/orders/:id',
    loggingMiddleware, // 添加日志中间件
    (req, res, query) => orderController.deleteOrder(req, res, query)
  );

  return router;
}