package com.github.microwind.springboot4ddd.interfaces.controller;

import com.github.microwind.springboot4ddd.application.dto.OrderDTO;
import com.github.microwind.springboot4ddd.application.service.OrderApplicationService;
import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import com.github.microwind.springboot4ddd.interfaces.annotation.RequireSign;
import com.github.microwind.springboot4ddd.interfaces.annotation.WithParams;
import com.github.microwind.springboot4ddd.interfaces.vo.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 *
 * @author jarry
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    /**
     * 创建订单（需要签名验证，带参数）
     */
    @PostMapping("/create")
    @RequireSign(withParams = WithParams.TRUE)
    public ApiResponse<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDTO order = orderApplicationService.createOrder(request);
        return ApiResponse.success(order);
    }

    /**
     * 获取订单详情（需要签名验证，不带参数）
     */
    @GetMapping("/{id}")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderDTO> getOrder(@PathVariable Long id) {
        try {
            OrderDTO order = orderApplicationService.getOrder(id);
            return ApiResponse.success(order);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户订单列表（需要签名验证）
     */
    @GetMapping("/user/{userId}")
    @RequireSign
    public ApiResponse<List<OrderDTO>> getUserOrders(@PathVariable Long userId) {
        List<OrderDTO> orders = orderApplicationService.getUserOrders(userId);
        return ApiResponse.success(orders);
    }

    /**
     * 获取所有订单
     */
    @GetMapping("/list")
    @RequireSign
    public ApiResponse<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderApplicationService.getAllOrders();
        return ApiResponse.success(orders);
    }

    /**
     * 取消订单（需要签名验证，带参数）
     */
    @PostMapping("/{id}/cancel")
    @RequireSign(withParams = WithParams.TRUE)
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long id) {
        try {
            OrderDTO order = orderApplicationService.cancelOrder(id);
            return ApiResponse.success(order);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 支付订单（需要签名验证，带参数）
     */
    @PostMapping("/{id}/pay")
    @RequireSign(withParams = WithParams.TRUE)
    public ApiResponse<OrderDTO> payOrder(@PathVariable Long id) {
        try {
            OrderDTO order = orderApplicationService.payOrder(id);
            return ApiResponse.success(order);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 完成订单（需要签名验证，带参数）
     */
    @PostMapping("/{id}/complete")
    @RequireSign(withParams = WithParams.TRUE)
    public ApiResponse<OrderDTO> completeOrder(@PathVariable Long id) {
        try {
            OrderDTO order = orderApplicationService.completeOrder(id);
            return ApiResponse.success(order);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除订单（需要签名验证）
     */
    @DeleteMapping("/{id}")
    @RequireSign
    public ApiResponse<Void> deleteOrder(@PathVariable Long id) {
        try {
            orderApplicationService.deleteOrder(id);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
