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

/**
 * 订单 HTTP 控制器。
 *
 * <p>DDD 四层架构中的<b>接口层（Interfaces）</b>。职责：
 * <ul>
 *   <li>解析 HTTP 请求（路径参数、查询串、请求体）</li>
 *   <li>装配 Command 对象，调用应用服务（只调一次，避免业务逻辑泄漏到此处）</li>
 *   <li>按异常类型分支映射 HTTP 状态码</li>
 * </ul>
 *
 * <h3>领域异常 → HTTP 状态码映射</h3>
 * <ul>
 *   <li>{@link InvalidOrderInputException}  → 400 Bad Request</li>
 *   <li>{@link OrderNotFoundException}      → 404 Not Found</li>
 *   <li>{@link InvalidOrderStateException}  → 409 Conflict</li>
 *   <li>其它 {@link Exception}               → 500 Internal Server Error</li>
 * </ul>
 */
public class OrderController {

    private final OrderService orderService;

    /**
     * 构造器注入：在 {@link com.microwind.javaweborder.Application} 装配阶段
     * 把已组装好的 {@link OrderService} 注入。
     *
     * @param orderService 已装配好的应用服务
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单：{@code POST /orders}。
     */
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

    /**
     * 获取订单：{@code GET /orders/:id}。
     */
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

    /**
     * 更新订单：{@code PUT /orders/:id}。
     */
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

    /**
     * 删除订单：{@code DELETE /orders/:id}。
     */
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

    /**
     * 获取订单列表：{@code GET /orders}。
     */
    public void listOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            List<OrderDTO> orders = orderService.listOrder();
            ResponseUtils.sendJsonResponse(response, 200, "查询成功", orders, null);
        } catch (Exception e) {
            ResponseUtils.sendJsonError(response, 500, "内部错误：" + e.getMessage(), null);
        }
    }

    // === 内部辅助：参数提取 / 校验 ===
    // 这些方法抛领域异常 InvalidOrderInputException，让接口层的 catch 子句保持精简、统一。

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

    /**
     * 请求体 JSON 映射结构（接口层内部使用，不暴露到其他层）。
     */
    private static class OrderRequest {
        private String customerName;
        private Object amount;

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
