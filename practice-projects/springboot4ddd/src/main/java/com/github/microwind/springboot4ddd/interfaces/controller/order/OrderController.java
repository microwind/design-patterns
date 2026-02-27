package com.github.microwind.springboot4ddd.interfaces.controller.order;

import com.github.microwind.springboot4ddd.application.dto.order.OrderDTO;
import com.github.microwind.springboot4ddd.application.service.order.OrderService;
import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import com.github.microwind.springboot4ddd.interfaces.annotation.RequireSign;
import com.github.microwind.springboot4ddd.interfaces.annotation.WithParams;
import com.github.microwind.springboot4ddd.interfaces.vo.order.CreateOrderRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderResponse;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ApiResponse<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDTO order = orderService.createOrder(request);
        return ApiResponse.success(order);
    }

    /**
     * 获取订单详情
     * 不返回用户详情（因为没有跨库查询）
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderDetail(id);
        return ApiResponse.success(order);
    }

    /**
     * 获取用户订单列表
     * 返回包含用户详情的订单信息（进行了跨库查询）
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderListResponse>> getUserOrders(@PathVariable Long userId) {
        List<OrderListResponse> orders = orderService.getUserOrderList(userId);
        return ApiResponse.success(orders);
    }

    /**
     * 分页查询用户订单列表
     * 返回包含用户详情的订单信息（进行了跨库查询）
     */
    @GetMapping("/user/{userId}/page")
    public ApiResponse<Page<OrderListResponse>> getUserOrdersByPage(@PathVariable Long userId, Pageable pageable) {
        validatePageable(pageable);
        Page<OrderListResponse> orders = orderService.getUserOrderList(userId, pageable);
        return ApiResponse.success(orders);
    }

    /**
     * 获取所有订单
     * 返回包含用户详情的订单信息（进行了跨库查询）
     */
    @GetMapping("/list")
    public ApiResponse<List<OrderListResponse>> listAllOrders() {
        List<OrderListResponse> orders = orderService.getAllOrderList();
        return ApiResponse.success(orders);
    }

    /**
     * 分页查询所有订单
     * 返回包含用户详情的订单信息（进行了跨库查询）
     */
    @GetMapping("/page")
//    @RequireSign
    public ApiResponse<Page<OrderListResponse>> listAllOrdersByPage(Pageable pageable) {
        validatePageable(pageable);
        Page<OrderListResponse> orders = orderService.getAllOrderList(pageable);
        return ApiResponse.success(orders);
    }

    /**
     * 验证分页参数
     */
    private void validatePageable(Pageable pageable) {
        if (pageable.getPageNumber() < 1) {
            throw new IllegalArgumentException("分页参数 page 必须从 1 开始，当前值: " + pageable.getPageNumber());
        }
        if (pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("分页参数 size 必须大于 0，当前值: " + pageable.getPageSize());
        }
    }

    /**
     * 取消订单（需要签名验证，不带参数）
     */
    @PostMapping("/{id}/cancel")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long id) {
        OrderDTO order = orderService.cancelOrder(id);
        return ApiResponse.success(order);
    }

    /**
     * 支付订单（需要签名验证，不带参数）
     */
    @PostMapping("/{id}/pay")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderDTO> payOrder(@PathVariable Long id) {
        OrderDTO order = orderService.payOrder(id);
        return ApiResponse.success(order);
    }

    /**
     * 完成订单（需要签名验证，不带参数）
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
