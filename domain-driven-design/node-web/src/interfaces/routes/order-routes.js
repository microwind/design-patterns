// 接口层（Interfaces）：订单路由设置
// src/interfaces/routes/order-routes.js
import {
  createRouter
} from './router.js';

export default function orderRoutes(orderController, loggingMiddleware) {
  const router = createRouter();

  router.post(
    '/orders',
    loggingMiddleware,
    (req, res) => orderController.createOrder(req, res)
  );

  router.get(
    '/orders/:id',
    loggingMiddleware,
    (req, res) => orderController.getOrder(req, res, req.query)
  );

  router.put(
    '/orders/:id',
    loggingMiddleware,
    (req, res) => orderController.updateOrder(req, res, req.query)
  );

  router.delete(
    '/orders/:id',
    loggingMiddleware,
    (req, res) => orderController.deleteOrder(req, res, req.query)
  );

  return router;
}