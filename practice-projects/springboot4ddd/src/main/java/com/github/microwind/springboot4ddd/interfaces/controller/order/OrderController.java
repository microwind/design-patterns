package com.github.microwind.springboot4ddd.interfaces.controller.order;

import com.github.microwind.springboot4ddd.application.dto.order.OrderDTO;
import com.github.microwind.springboot4ddd.application.service.order.OrderService;
import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import com.github.microwind.springboot4ddd.interfaces.annotation.RequireSign;
import com.github.microwind.springboot4ddd.interfaces.annotation.WithParams;
import com.github.microwind.springboot4ddd.interfaces.vo.order.CreateOrderRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderResponse;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderListResponse;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
     * 获取订单详情（支持缓存穿透）
     * 优先从缓存获取，缓存未命中时从数据库加载并缓存
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        log.info("查询订单详情，订单ID: {} - 开始缓存穿透查询", id);
        long startTime = System.currentTimeMillis();
        
        OrderResponse order = orderService.getOrderDetail(id);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        String message;
        if (duration < 50) { // 假设缓存命中响应时间小于50ms
            message = String.format("获取订单成功（缓存命中），耗时: %dms", duration);
            log.info("订单ID: {} 缓存命中，响应时间: {}ms", id, duration);
        } else {
            message = String.format("获取订单成功（数据库查询），耗时: %dms", duration);
            log.info("订单ID: {} 缓存未命中，从数据库加载并缓存，响应时间: {}ms", id, duration);
        }
        
        return ApiResponse.success(message, order);
    }

    /**
     * 根据订单号获取订单详情（支持缓存穿透）
     * 优先从缓存获取，缓存未命中时从数据库加载并缓存
     */
    @GetMapping("/no/{orderNo}")
    public ApiResponse<OrderDTO> getOrderByNo(@PathVariable String orderNo) {
        log.info("查询订单详情，订单号: {} - 开始缓存穿透查询", orderNo);
        long startTime = System.currentTimeMillis();
        
        OrderDTO order = orderService.getOrderByNo(orderNo);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        String message;
        if (duration < 50) { // 假设缓存命中响应时间小于50ms
            message = String.format("获取订单成功（缓存命中），耗时: %dms", duration);
            log.info("订单号: {} 缓存命中，响应时间: {}ms", orderNo, duration);
        } else {
            message = String.format("获取订单成功（数据库查询），耗时: %dms", duration);
            log.info("订单号: {} 缓存未命中，从数据库加载并缓存，响应时间: {}ms", orderNo, duration);
        }
        
        return ApiResponse.success(message, order);
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
    @GetMapping("")
    public ApiResponse<List<OrderListResponse>> getAllOrders() {
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
     * 通过SHA256(ios1secret&!_caller1/api/orders/{id}/pay1772198389223)得到sign
     * fetch示例:
     * fetch("http://127.0.0.1:8080/api/orders/5/pay", {
     *    method: "POST",
     *    headers: { "Content-Type": "application/json",
     *    "Sign-appCode": "ios1", "Sign-path": "/api/orders/{id}/pay",
     *    "Sign-sign": "18b463ec7d1d92981bc0f183aa099c2c6c2ecf773f679845c09f456338af6f97",
     *    "Sign-time": 1772198389223}
     * })
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
