// 接口层（Interfaces）：订单 HTTP 控制器
package com.microwind.javaweborder.interfaces.controllers;

import com.microwind.javaweborder.application.dto.OrderDTO;
import com.microwind.javaweborder.application.services.OrderService;
import com.microwind.javaweborder.infrastructure.message.MessageQueueService;
import com.microwind.javaweborder.infrastructure.repository.OrderRepositoryImpl;
import com.microwind.javaweborder.utils.BodyParserUtils;
import com.microwind.javaweborder.utils.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class OrderController {

    private final OrderService orderService;

    public OrderController() {
        this.orderService = new OrderService(new OrderRepositoryImpl(), new MessageQueueService());
    }

    // 创建订单：对应 POST /orders
    public void createOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        OrderRequest body = new OrderRequest();
        try {
            // 解析请求体
            body = BodyParserUtils.parseRequestBody(request, OrderRequest.class);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "请求错误：" + e.getMessage(), null);
        }

        try {
            String customerName = body.getCustomerName();
            // 将订单金额转换为数字，校验是否有效
            double amount = parseAmount(body.getAmount());
            OrderDTO order = orderService.createOrder(customerName, amount);
            ResponseUtils.sendJsonResponse(response, 201,"创建成功", order, null);
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // 获取订单：对应 GET /orders/:id
    public void getOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = extractId(request);
            long orderId = parseId(idParam);
            OrderDTO order = orderService.getOrder(orderId);
            ResponseUtils.sendJsonResponse(response, 200, "获取成功", order, null);
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // 更新订单：对应 PUT /orders/:id
    public void updateOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OrderRequest body = BodyParserUtils.parseRequestBody(request, OrderRequest.class);
            String idParam = extractId(request);
            long orderId = parseId(idParam);

            double amount = parseAmount(body.getAmount());

            OrderDTO order = orderService.updateOrder(orderId, body.getCustomerName(), amount);
            ResponseUtils.sendJsonResponse(response, 200,"更新成功", order, null);
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // 删除订单：对应 DELETE /orders/:id
    public void deleteOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idParam = extractId(request);
            long orderId = parseId(idParam);
            orderService.deleteOrder(orderId);
            ResponseUtils.sendNoContent(response);
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // 获取订单：对应 GET /orders
    public void listOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<OrderDTO> orders = orderService.listOrder();
            ResponseUtils.sendJsonResponse(response, 200,"查询成功", orders, null);
        } catch (IllegalArgumentException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
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
    private long parseId(String idParam) {
        try {
            return Long.parseLong(idParam);
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
