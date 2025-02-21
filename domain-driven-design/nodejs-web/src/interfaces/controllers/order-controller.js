// 接口层（Interfaces）：订单 HTTP 处理器
// order_handler.js
const { parseBody } = require('../../utils/body_parser');

class OrderController {
  constructor(orderService) {
    this.orderService = orderService;
  }

  createOrder(req, res) {
    parseBody(req, (body) => {
      const { customer_name, total_amount } = body;
      this.orderService.createOrder(customer_name, total_amount)
        .then(order => {
          res.writeHead(201, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify(order));
        })
        .catch(err => {
          res.writeHead(400, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ error: err.message }));
        });
    });
  }

  getOrder(req, res, query) {
    const { id } = query;
    this.orderService.getOrder(id)
      .then(order => {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify(order));
      })
      .catch(err => {
        res.writeHead(404, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: err.message }));
      });
  }

  updateOrder(req, res, query) {
    parseBody(req, (body) => {
      const { id } = query;
      const { customer_name, total_amount } = body;
      this.orderService.updateOrder(id, customer_name, total_amount)
        .then(order => {
          res.writeHead(200, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify(order));
        })
        .catch(err => {
          res.writeHead(400, { 'Content-Type': 'application/json' });
          res.end(JSON.stringify({ error: err.message }));
        });
    });
  }

  deleteOrder(req, res, query) {
    const { id } = query;
    this.orderService.deleteOrder(id)
      .then(() => {
        res.writeHead(204);
        res.end();
      })
      .catch(err => {
        res.writeHead(404, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: err.message }));
      });
  }
}

module.exports = { OrderController };