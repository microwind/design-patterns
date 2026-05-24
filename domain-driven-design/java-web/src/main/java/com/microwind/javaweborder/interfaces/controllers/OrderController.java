// 接口层(Interfaces) - 订单 HTTP 控制器
//
// 接口层职责：
// - 解析 HTTP 请求（路径参数、查询串、请求体）
// - 调用应用服务（且只调一次，避免业务逻辑泄漏到此处）
// - 把结果序列化为响应（按异常类型分支映射 HTTP 状态码）
//
// 异常映射规则：
// - InvalidOrderInputException  → 400 Bad Request（值对象 / 参数校验失败）
// - OrderNotFoundException      → 404 Not Found（订单不存在）
// - InvalidOrderStateException  → 409 Conflict（业务状态不允许）
// - 其它 Exception              → 500 Internal Server Error
package com.microwind.javaweborder.interfaces.controllers;

import com.microwind.javaweborder.application.command.CreateOrderCommand;
import com.microwind.javaweborder.application.command.UpdateOrderCommand;
import com.microwind.javaweborder.application.dto.OrderDTO;
import com.microwind.javaweborder.application.services.OrderService;
import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;
import com.microwind.javaweborder.domain.exception.InvalidOrderStateException;
import com.microwind.javaweborder.domain.exception.OrderNotFoundException;
import com.microwind.javaweborder.utils.BodyParserUtils;
import com.microwind.javaweborder.utils.ResponseUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class OrderController {

    private final OrderService orderService;

    // 构造器注入：在 Application 装配阶段把已组装好的 OrderService 注进来
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 创建订单：对应 POST /orders
    public void createOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OrderRequest body = BodyParserUtils.parseRequestBody(request, OrderRequest.class);
            CreateOrderCommand command = new CreateOrderCommand(
                    body.getCustomerName(),
                    parseAmount(body.getAmount())
            );
            OrderDTO order = orderService.createOrder(command);
            ResponseUtils.sendJsonResponse(response, 201, "创建成功", order, null);
        } catch (InvalidOrderInputException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // 获取订单：对应 GET /orders/:id
    public void getOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            long orderId = parseId(extractId(request));
            OrderDTO order = orderService.getOrder(orderId);
            ResponseUtils.sendJsonResponse(response, 200, "获取成功", order, null);
        } catch (InvalidOrderInputException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (OrderNotFoundException e) {
            ResponseUtils.sendJsonError(response, 404, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // 更新订单：对应 PUT /orders/:id
    public void updateOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            OrderRequest body = BodyParserUtils.parseRequestBody(request, OrderRequest.class);
            long orderId = parseId(extractId(request));
            UpdateOrderCommand command = new UpdateOrderCommand(
                    orderId,
                    body.getCustomerName(),
                    parseAmount(body.getAmount())
            );
            OrderDTO order = orderService.updateOrder(command);
            ResponseUtils.sendJsonResponse(response, 200, "更新成功", order, null);
        } catch (InvalidOrderInputException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (InvalidOrderStateException e) {
            ResponseUtils.sendJsonError(response, 409, e.getMessage(), null);
        } catch (OrderNotFoundException e) {
            ResponseUtils.sendJsonError(response, 404, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // 删除订单：对应 DELETE /orders/:id
    public void deleteOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            long orderId = parseId(extractId(request));
            orderService.deleteOrder(orderId);
            ResponseUtils.sendNoContent(response);
        } catch (InvalidOrderInputException e) {
            ResponseUtils.sendJsonError(response, 400, e.getMessage(), null);
        } catch (OrderNotFoundException e) {
            ResponseUtils.sendJsonError(response, 404, e.getMessage(), null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // 获取订单列表：对应 GET /orders
    public void listOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<OrderDTO> orders = orderService.listOrder();
            ResponseUtils.sendJsonResponse(response, 200, "查询成功", orders, null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // === 内部辅助：参数提取 / 校验 ===
    //
    // 这里抛的也是领域异常 InvalidOrderInputException，
    // 这样接口层的 catch 子句保持精简、统一。

    private String extractId(HttpServletRequest request) {
        Object idObj = request.getAttribute("id");
        String idParam = idObj != null ? idObj.toString() : request.getParameter("id");
        if (idParam == null || idParam.isEmpty()
                || "undefined".equals(idParam) || "null".equals(idParam)) {
            throw new InvalidOrderInputException("订单 ID 不能为空");
        }
        return idParam;
    }

    private long parseId(String idParam) {
        try {
            return Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            throw new InvalidOrderInputException("订单 ID 无效");
        }
    }

    private double parseAmount(Object amountObj) {
        if (amountObj == null) {
            throw new InvalidOrderInputException("订单金额不能为空");
        }
        try {
            return Double.parseDouble(amountObj.toString());
        } catch (NumberFormatException e) {
            throw new InvalidOrderInputException("订单金额无效");
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
