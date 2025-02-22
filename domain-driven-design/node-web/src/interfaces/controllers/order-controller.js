// 接口层（Interfaces）：订单 HTTP 处理器
// src/interfaces/controllers/order-controller.js

import { parseBody } from '../../utils/body-parser.js';

export default class OrderController {
  constructor(orderService) {
    this.orderService = orderService;
  }

  // 创建订单
  async createOrder(req, res) {
    try {
      const body = await parseBody(req);
      const { customerName, amount } = body;
      const order = await this.orderService.createOrder(customerName, amount);
      res.writeHead(201, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(order));
    } catch (error) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: error.message }));
    }
  }

  // 获取订单
  async getOrder(req, res, query) {
    try {
      const { id } = query;
      const order = await this.orderService.getOrder(parseInt(id));
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(order));
    } catch (error) {
      res.writeHead(404, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: error.message }));
    }
  }

  // 更新订单
  async updateOrder(req, res, query) {
    try {
      const body = await parseBody(req);
      const { id } = query;
      const { customerName, amount } = body;
      const order = await this.orderService.updateOrder(parseInt(id), customerName, parseFloat(amount));
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify(order));
    } catch (error) {
      res.writeHead(400, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: error.message }));
    }
  }

  // 删除订单
  async deleteOrder(req, res, query) {
    try {
      const { id } = query;
      await this.orderService.deleteOrder(parseInt(id));
      res.writeHead(204);
      res.end();
    } catch (error) {
      res.writeHead(404, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ error: error.message }));
    }
  }
}