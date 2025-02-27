// 入口页面
// nodejs-web/index.js

import http from 'http';
import url from 'url';
import OrderController from './src/interfaces/controllers/order-controller.js';
import OrderService from './src/application/services/order-service.js';
import OrderRepository from './src/infrastructure/repository/order-repository.js';
import loggingMiddleware from './src/middleware/logging-middleware.js';
import serverConfig from './src/config/server-config.js';
import orderRoutes from './src/interfaces/routes/order-routes.js';

// 初始化依赖
const orderRepository = new OrderRepository();
const orderService = new OrderService(orderRepository);
const orderController = new OrderController(orderService);

const port = serverConfig.port;

// 初始化路由
const router = orderRoutes(orderController, loggingMiddleware);

// 创建 HTTP 服务器
const server = http.createServer((req, res) => {
  if (req.method === 'GET' && req.url === '/') {
    // 首页
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(`
      <h1>Welcome to DDD example.</h1>
      <pre>
        测试
        <code>
        创建：curl -X POST "http://localhost:${port}/api/api/orders" -H "Content-Type: application/json" -d '{"customerName": "齐天大圣", "amount": 99.99}'
        查询：curl -X GET "http://localhost:${port}/api/orders/订单号"
        更新：curl -X PUT "http://localhost:${port}/api/orders/订单号" -H "Content-Type: application/json" -d '{"customerName": "孙悟空", "amount": 11.22}'
        删除：curl -X DELETE "http://localhost:${port}/api/orders/订单号"
        查询：curl -X GET "http://localhost:${port}/api/orders/订单号"
        </code>
        详细：https://github.com/microwind/design-patterns/tree/main/domain-driven-design
      </pre>
    `);
  } else {
    // 处理路由
    router.handleRequest(req, res);
  }
});

// 启动服务器
server.listen(port, () => {
  console.log(`Starting server on :${port} successfully.`);
});

/*
jarry@Mac node-web % node index.js
Starting server on :8080 successfully.
REQUEST: POST /api/orders took 3ms
REQUEST: GET /api/orders/1740220487386921 took 3ms
REQUEST: PUT /api/orders/%e8%ae%a2%e5%8d%95%e5%8f%b7 took 5ms
订单 ID 1740220487386921 的客户名称已更新为: 孙悟空
订单 ID 1740220487386921 的金额已更新为: 11.22
REQUEST: PUT /api/orders/1740220487386921 took 7ms
REQUEST: GET /api/orders/1740220487386921 took 3ms
REQUEST: DELETE /api/orders/1740220487386921 took 1ms
REQUEST: GET /api/orders/1740220487386921 took 8ms
*/