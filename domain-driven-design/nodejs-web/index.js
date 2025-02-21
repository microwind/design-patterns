// 入口页面
// index.js
const http = require('http');
const url = require('url');
const { OrderHandler } = require('./internal/interfaces/handlers/order_handler');
const { loggingMiddleware } = require('./internal/middleware/logging_middleware');

const port = 8080;
const orderHandler = new OrderHandler();

const server = http.createServer((req, res) => {
  loggingMiddleware(req, res, () => {
    const parsedUrl = url.parse(req.url, true);
    const { pathname, query } = parsedUrl;

    if (req.method === 'GET' && pathname === '/') {
      res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
      res.end(`
        <h1>Welcome to DDD example.</h1>
        <pre>
          测试
          <code>
          创建：curl -X POST "http://localhost:${port}/orders/create" -H "Content-Type: application/json" -d '{"customer_name": "齐天大圣", "total_amount": 99.99}'
          查询：curl -X GET "http://localhost:${port}/orders/get?id=订单号"
          更新：curl -X PUT "http://localhost:${port}/orders/update?id=订单号" -H "Content-Type: application/json" -d '{"customer_name": "孙悟空", "total_amount": 11.22}'
          删除：curl -X DELETE "http://localhost:${port}/orders/delete?id=订单号"
          查询：curl -X GET "http://localhost:${port}/orders/get?id=订单号"
          </code>
        </pre>
      `);
    } else if (req.method === 'POST' && pathname === '/orders/create') {
      orderHandler.createOrder(req, res);
    } else if (req.method === 'GET' && pathname === '/orders/get') {
      orderHandler.getOrder(req, res, query);
    } else if (req.method === 'PUT' && pathname === '/orders/update') {
      orderHandler.updateOrder(req, res, query);
    } else if (req.method === 'DELETE' && pathname === '/orders/delete') {
      orderHandler.deleteOrder(req, res, query);
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('Not Found');
    }
  });
});

server.listen(port, () => {
  console.log(`Starting server on :${port} successfully.`);
});