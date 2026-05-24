package com.github.microwind.springboot4ddd.interfaces.controller.order;

import com.github.microwind.springboot4ddd.application.command.order.CreateOrderCommand;
import com.github.microwind.springboot4ddd.application.dto.order.OrderDTO;
import com.github.microwind.springboot4ddd.application.dto.order.OrderListView;
import com.github.microwind.springboot4ddd.application.service.order.OrderService;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import com.github.microwind.springboot4ddd.interfaces.annotation.RequireSign;
import com.github.microwind.springboot4ddd.interfaces.annotation.WithParams;
import com.github.microwind.springboot4ddd.interfaces.page.PageableConverter;
import com.github.microwind.springboot4ddd.interfaces.vo.order.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        CreateOrderCommand command = CreateOrderCommand.builder()
                .userId(request.getUserId())
                .totalAmount(request.getTotalAmount())
                .build();
        OrderDTO order = orderService.createOrder(command);
        return ApiResponse.success(order);
    }

    /**
     * 获取订单详情（支持缓存穿透）
     * 优先从缓存获取，缓存未命中时从数据库加载并缓存
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderDTO> getOrder(@PathVariable Long id) {
        log.info("查询订单详情，订单ID: {}", id);
        OrderDTO order = orderService.getOrderDetail(id);
        return ApiResponse.success("获取订单成功", order);
    }

    /**
     * 根据订单号获取订单详情（支持缓存穿透）
     * 优先从缓存获取，缓存未命中时从数据库加载并缓存
     */
    @GetMapping("/no/{orderNo}")
    public ApiResponse<OrderDTO> getOrderByNo(@PathVariable String orderNo) {
        log.info("查询订单详情，订单号: {}", orderNo);
        OrderDTO order = orderService.getOrderByNo(orderNo);
        return ApiResponse.success("获取订单成功", order);
    }

    /**
     * 获取用户订单列表（包含跨上下文的用户信息）
     */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<OrderListView>> getUserOrders(@PathVariable Long userId) {
        List<OrderListView> orders = orderService.getUserOrderList(userId);
        return ApiResponse.success(orders);
    }

    @GetMapping("/user/{userId}/page")
    public ApiResponse<PageResult<OrderListView>> getUserOrdersByPage(@PathVariable Long userId, Pageable pageable) {
        validatePageable(pageable);
        PageResult<OrderListView> orders = orderService.getUserOrderList(userId, PageableConverter.toDomain(pageable));
        return ApiResponse.success(orders);
    }

    /**
     * 获取所有订单列表（包含跨上下文的用户信息）
     */
    @GetMapping
    public ApiResponse<List<OrderListView>> getAllOrders() {
        List<OrderListView> orders = orderService.getAllOrderList();
        return ApiResponse.success(orders);
    }

    /**
     * 分页查询所有订单
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<OrderListView>> listAllOrdersByPage(Pageable pageable) {
        validatePageable(pageable);
        PageResult<OrderListView> orders = orderService.getAllOrderList(PageableConverter.toDomain(pageable));
        return ApiResponse.success(orders);
    }

    private void validatePageable(Pageable pageable) {
        if (pageable.getPageNumber() < 1) {
            throw new IllegalArgumentException("分页参数 page 必须从 1 开始，当前值: " + pageable.getPageNumber());
        }
        if (pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("分页参数 size 必须大于 0，当前值: " + pageable.getPageSize());
        }
    }

    @PostMapping("/{id}/cancel")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long id) {
        OrderDTO order = orderService.cancelOrder(id);
        return ApiResponse.success(order);
    }

    @PostMapping("/{id}/pay")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderDTO> payOrder(@PathVariable Long id) {
        OrderDTO order = orderService.payOrder(id);
        return ApiResponse.success(order);
    }

    @PostMapping("/{id}/complete")
    @RequireSign(withParams = WithParams.FALSE)
    public ApiResponse<OrderDTO> completeOrder(@PathVariable Long id) {
        OrderDTO order = orderService.completeOrder(id);
        return ApiResponse.success(order);
    }

    @DeleteMapping("/{id}")
    @RequireSign
    public ApiResponse<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.success(null);
    }
}
