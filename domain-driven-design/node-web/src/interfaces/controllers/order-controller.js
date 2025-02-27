// 接口层（Interfaces）：订单 HTTP 处理器
// src/interfaces/controllers/order-controller.js
import { parseBody } from '../../utils/body-parser.js';
import { sendResponse, sendError, sendNoContent } from '../../utils/response.js';

export default class OrderController {
  constructor(orderService) {
    this.orderService = orderService;
  }

  // 创建订单
  async createOrder(req, res) {
    try {
      const body = await parseBody(req);
      const { customerName, amount } = body;

      // 验证订单金额
      const amountNumber = Number(amount);
      if (isNaN(amountNumber)) {
        throw new Error('订单金额无效');
      }

      const order = await this.orderService.createOrder(customerName, amountNumber);
      sendResponse(res, 201, order);
    } catch (error) {
      sendError(res, 400, error.message);
    }
  }

  // 获取订单
  async getOrder(req, res, query) {
    try {
      const { id } = query;
      if (!id || id === 'undefined' || id === 'null') {
        throw new Error('订单 ID 不能为空');
      }

      const orderId = Number(id);
      if (!Number.isInteger(orderId)) {
        throw new Error('订单 ID 无效');
      }

      const order = await this.orderService.getOrder(orderId);
      sendResponse(res, 200, order);
    } catch (error) {
      sendError(res, 404, error.message);
    }
  }

  // 更新订单
  async updateOrder(req, res, query) {
    try {
      const body = await parseBody(req);
      const { id } = query;

      if (!id || id === 'undefined' || id === 'null') {
        throw new Error('订单 ID 不能为空');
      }

      const orderId = Number(id);
      if (!Number.isInteger(orderId)) {
        throw new Error('订单 ID 无效');
      }

      const amountNumber = Number(body.amount);
      if (isNaN(amountNumber)) {
        throw new Error('订单金额无效');
      }

      const order = await this.orderService.updateOrder(orderId, body.customerName, amountNumber);
      sendResponse(res, 200, order);
    } catch (error) {
      sendError(res, 400, error.message);
    }
  }

  // 删除订单
  async deleteOrder(req, res, query) {
    try {
      const { id } = query;
      if (!id || id === 'undefined' || id === 'null') {
        throw new Error('订单 ID 不能为空');
      }

      const orderId = Number(id);
      if (!Number.isInteger(orderId)) {
        throw new Error('订单 ID 无效');
      }

      await this.orderService.deleteOrder(orderId);
      sendNoContent(res);
    } catch (error) {
      sendError(res, 404, error.message);
    }
  }
}

