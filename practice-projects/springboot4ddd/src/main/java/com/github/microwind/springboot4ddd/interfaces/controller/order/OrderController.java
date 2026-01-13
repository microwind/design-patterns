package com.github.microwind.springboot4ddd.interfaces.controller.order;

import com.github.microwind.springboot4ddd.application.dto.order.OrderDTO;
import com.github.microwind.springboot4ddd.application.service.order.OrderService;
import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import com.github.microwind.springboot4ddd.interfaces.annotation.IgnoreSignHeader;
import com.github.microwind.springboot4ddd.interfaces.annotation.RequireSign;
import com.github.microwind.springboot4ddd.interfaces.annotation.WithParams;
import com.github.microwind.springboot4ddd.interfaces.vo.order.CreateOrderRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderResponse;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderListResponse;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单（需要签名验证，带参数）
     */
    @PostMapping("/create")
    @RequireSign(withParams = WithParams.TRUE)
    public ApiResponse<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDTO order = orderService.createOrder(request);
        return ApiResponse.success(order);
    }

    /**
     * 获取订单详情（需要签名验证，不带参数）
     * 不返回用户详情（因为没有跨库查询）
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderDetail(id);
        return ApiResponse.success(order);
    }

    /**
     * 获取用户订单列表（需要签名验证）
     * 返回包含用户详情的订单信息（进行了跨库查询）
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderListResponse>> getUserOrders(@PathVariable Long userId) {
        List<OrderListResponse> orders = orderService.getUserOrderList(userId);
        return ApiResponse.success(orders);
    }

    /**
     * 获取所有订单
     * 返回包含用户详情的订单信息（进行了跨库查询）
     */
    @GetMapping("/list")
//    @RequireSign
    public ApiResponse<List<OrderListResponse>> getAllOrders() {
        List<OrderListResponse> orders = orderService.getAllOrderList();
        return ApiResponse.success(orders);
    }

    /**
     * 取消订单（需要签名验证，不带参数测试）
     */
    @PostMapping("/{id}/cancel")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long id) {
        OrderDTO order = orderService.cancelOrder(id);
        return ApiResponse.success(order);
    }

    /**
     * 支付订单（需要签名验证，带参数）
     */
    @PostMapping("/{id}/pay")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderDTO> payOrder(@PathVariable Long id) {
        OrderDTO order = orderService.payOrder(id);
        return ApiResponse.success(order);
    }

    /**
     * 完成订单（需要签名验证，带参数）
     */
    @PostMapping("/{id}/complete")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderDTO> completeOrder(@PathVariable Long id) {
        OrderDTO order = orderService.completeOrder(id);
        return ApiResponse.success(order);
    }

    /**
     * 删除订单（需要签名验证）
     */
    @DeleteMapping("/{id}")
    @RequireSign
    public ApiResponse<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success(null);
    }
}
