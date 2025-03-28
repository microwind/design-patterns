// order 路由测试文件
// test/interfaces/routes/order-routes.test.js

import http from 'http';
import OrderController from '../../../src/interfaces/controllers/order-controller.js';
import OrderService from '../../../src/application/services/order-service.js';
import { OrderRepositoryImpl as OrderRepository } from '../../../src/infrastructure/repository/order-repository-impl.js';
import loggingMiddleware from '../../../src/middleware/logging-middleware.js';
import serverConfig from '../../../src/config/server-config.js';
import { setupLogging } from '../../../src/utils/logging.js';
import orderRoutes from '../../../src/interfaces/routes/order-routes.js';

// 定义统一的 api 前缀
const apiPrefix = '/api';

// 初始化依赖
const orderRepository = new OrderRepository();
const orderService = new OrderService(orderRepository);
const orderController = new OrderController(orderService);

// 初始化路由
const router = orderRoutes(orderController, loggingMiddleware);

  // 初始化日志文件
  setupLogging(serverConfig.logging.file);

// 创建 HTTP 服务器
const server = http.createServer((req, res) => {
  router.handleRequest(req, res);
});

// 启动服务器
server.listen(8080, () => {
  console.log('测试服务器已启动，端口：8080');
});

// 测试工具函数：发送 HTTP 请求
function sendRequest(options, data = null) {
  return new Promise((resolve, reject) => {
    const req = http.request(options, (res) => {
      let responseData = '';

      res.on('data', (chunk) => {
        responseData += chunk;
      });

      res.on('end', () => {
        resolve({
          statusCode: res.statusCode,
          headers: res.headers,
          body: responseData,
        });
      });
    });

    req.on('error', (error) => {
      reject(error);
    });

    if (data) {
      req.write(data);
    }

    req.end();
  });
}

// 测试用例
(async () => {
  try {
    // 测试创建订单
    const createOrderResponse = await sendRequest({
        hostname: 'localhost',
        port: 8080,
        path: `${apiPrefix}/orders`,
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      },
      JSON.stringify({
        customerName: '齐天大圣',
        amount: 99.99
      })
    );

    console.log('创建订单测试结果：');
    console.log('状态码:', createOrderResponse.statusCode);
    console.log('响应体:', createOrderResponse.body);

    if (createOrderResponse.statusCode !== 201) {
      throw new Error('创建订单测试失败');
    }

    // 测试获取订单
    const orderId = JSON.parse(createOrderResponse.body).id;
    const getOrderResponse = await sendRequest({
      hostname: 'localhost',
      port: 8080,
      path: `${apiPrefix}/orders/${orderId}`,
      method: 'GET',
    });

    console.log('获取订单测试结果：');
    console.log('状态码:', getOrderResponse.statusCode);
    console.log('响应体:', getOrderResponse.body);

    if (getOrderResponse.statusCode !== 200) {
      throw new Error('获取订单测试失败');
    }

    // 测试更新订单
    const updateOrderResponse = await sendRequest({
        hostname: 'localhost',
        port: 8080,
        path: `${apiPrefix}/orders/${orderId}`,
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
      },
      JSON.stringify({
        customerName: '孙悟空',
        amount: 11.22
      })
    );

    console.log('更新订单测试结果：');
    console.log('状态码:', updateOrderResponse.statusCode);
    console.log('响应体:', updateOrderResponse.body);

    if (updateOrderResponse.statusCode !== 200) {
      throw new Error('更新订单测试失败');
    }

    // 测试删除订单
    const deleteOrderResponse = await sendRequest({
      hostname: 'localhost',
      port: 8080,
      path: `${apiPrefix}/orders/${orderId}`,
      method: 'DELETE',
    });

    console.log('删除订单测试结果：');
    console.log('状态码:', deleteOrderResponse.statusCode);

    if (deleteOrderResponse.statusCode !== 204) {
      throw new Error('删除订单测试失败');
    }

    // 测试获取所有订单
    const getAllOrdersResponse = await sendRequest({
      hostname: 'localhost',
      port: 8080,
      path: `${apiPrefix}/orders`,
      method: 'GET',
    });

    console.log('获取所有订单测试结果：');
    console.log('状态码:', getAllOrdersResponse.statusCode);
    console.log('响应体:', getAllOrdersResponse.body);

    if (getAllOrdersResponse.statusCode !== 200) {
      throw new Error('获取所有订单测试失败');
    }

    const orders = JSON.parse(getAllOrdersResponse.body);
    if (!Array.isArray(orders)) {
      throw new Error('获取所有订单接口返回的不是数组');
    }

    console.log(`当前订单数量: ${orders.length}`);

    console.log('所有测试通过！');
  } catch (error) {
    console.error('测试失败：', error);
  } finally {
    // 关闭服务器
    server.close(() => {
      console.log('测试服务器已关闭');
    });
  }
})();

/*
jarry@Mac node-web % node test/interfaces/routes/order-routes.test.js
测试服务器已启动，端口：8080
REQUEST: POST /api/orders took 3ms
创建订单测试结果：
状态码: 201
响应体: {"id":1740292885975985,"customerName":"齐天大圣","amount":99.99,"status":0}
REQUEST: GET /api/orders/1740292885975985 took 2ms
获取订单测试结果：
状态码: 200
响应体: {"id":1740292885975985,"customerName":"齐天大圣","amount":99.99,"status":0}
订单 ID 1740292885975985 的客户名称已更新为: 孙悟空
订单 ID 1740292885975985 的金额已更新为: 11.22
REQUEST: PUT /api/orders/1740292885975985 took 4ms
更新订单测试结果：
状态码: 200
响应体: {"id":1740292885975985,"customerName":"孙悟空","amount":11.22,"status":0}
REQUEST: DELETE /api/orders/1740292885975985 took 4ms
删除订单测试结果：
状态码: 204
REQUEST: GET /api/orders took 0ms
获取所有订单测试结果：
状态码: 200
响应体: []
当前订单数量: 0
所有测试通过！
测试服务器已关闭
*/