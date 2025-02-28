// 接口层（Interfaces）：订单 HTTP 控制器
package com.javaweborder.interfaces.controllers;

import com.javaweborder.application.dto.OrderDTO;
import com.javaweborder.application.services.OrderService;
import com.javaweborder.infrastructure.repository.OrderRepositoryImpl;
import com.javaweborder.utils.BodyParserUtils;
import com.javaweborder.utils.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderController {

    private final OrderService orderService;

    public OrderController() {
        this.orderService = new OrderService(new OrderRepositoryImpl());
    }

    // 创建订单：对应 POST /orders
    public void createOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 解析请求体
            OrderRequest body = BodyParserUtils.parseRequestBody(request, OrderRequest.class);
            String customerName = body.getCustomerName();

            // 将订单金额转换为数字，校验是否有效
            double amount = parseAmount(body.getAmount());

            OrderDTO order = orderService.createOrder(customerName, amount);
            ResponseUtils.sendJsonResponse(response, 201, order, null);
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "服务器内部错误", null);
        }
    }

    // 获取订单：对应 GET /orders/{id}
    public void getOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = extractId(request);
            int orderId = parseId(idParam);
            OrderDTO order = orderService.getOrder(orderId);
            ResponseUtils.sendJsonResponse(response, 200, order, null);
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "服务器内部错误", null);
        }
    }

    // 更新订单：对应 PUT /orders/{id}
    public void updateOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OrderRequest body = BodyParserUtils.parseRequestBody(request, OrderRequest.class);
            String idParam = extractId(request);
            int orderId = parseId(idParam);

            double amount = parseAmount(body.getAmount());

            OrderDTO order = orderService.updateOrder(orderId, body.getCustomerName(), amount);
            ResponseUtils.sendJsonResponse(response, 200, order, null);
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "服务器内部错误", null);
        }
    }

    // 删除订单：对应 DELETE /orders/{id}
    public void deleteOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = extractId(request);
            int orderId = parseId(idParam);
            orderService.deleteOrder(orderId);
            ResponseUtils.sendNoContent(response);
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "服务器内部错误", null);
        }
    }

    // 从请求中提取订单 ID（先从 Router 设置的属性中查找，再查 query 参数）
    private String extractId(HttpServletRequest request) {
        Object idObj = request.getAttribute("id");
        String idParam = idObj != null ? idObj.toString() : request.getParameter("id");
        if (idParam == null || idParam.isEmpty() ||
                "undefined".equals(idParam) || "null".equals(idParam)) {
            throw new IllegalArgumentException("订单 ID 不能为空");
        }
        return idParam;
    }

    // 将字符串转换为整数，校验合法性
    private int parseId(String idParam) {
        try {
            return Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("订单 ID 无效");
        }
    }

    // 将金额对象转换为 double 类型，校验合法性
    private double parseAmount(Object amountObj) {
        try {
            return Double.parseDouble(amountObj.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("订单金额无效");
        }
    }

    // 内部类，用于映射请求体 JSON 对象
    private static class OrderRequest {
        private String customerName;
        private Object amount; // 可支持数字或字符串

        public String getCustomerName() {
            return customerName;
        }
        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
        public Object getAmount() {
            return amount;
        }
        public void setAmount(Object amount) {
            this.amount = amount;
        }
    }
}
