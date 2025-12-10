package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.order.Order;
import com.microwind.knife.application.dto.order.OrderPageDTO;
import com.microwind.knife.application.dto.order.OrderWithItemsPageDTO;
import com.microwind.knife.application.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 创建订单
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    // 查询全部订单接口
    @GetMapping("")
    public OrderPageDTO getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orderPages = orderService.getAllOrders(PageRequest.of(page, size));
        return new OrderPageDTO(orderPages);
    }

    // 查询全部订单接口（包含订单项）
    @GetMapping("/with-items")
    public OrderWithItemsPageDTO getAllOrdersWithItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orderPages = orderService.getAllOrdersWithItems(PageRequest.of(page, size));
        return new OrderWithItemsPageDTO(orderPages);
    }

    // 根据订单号查询
//    @GetMapping("/{orderNo}")
    @GetMapping("/{orderNo:[A-Za-z0-9\\-]+}")
    public ApiResponse<Order> getOrder(@PathVariable String orderNo) {
        // return orderService.getByOrderNo(orderNo);
        // 或自定义ApiResponse返回
        Order order = orderService.getByOrderNo(orderNo);
        if (order != null) {
            return ApiResponse.success(order, "查询订单成功。");
        } else {
            return ApiResponse.failure(HttpStatus.NOT_FOUND.value(), "查询订单失败。");
        }
    }


    // 查询用户订单列表
    @GetMapping("/user/{userId}")
    public ApiResponse<List<Order>> getUserOrders(@PathVariable Long userId) {
        return new ApiResponse<>(HttpStatus.OK.value(), orderService.getUserOrders(userId), "根据用户查询订单。");
    }

    // 更新接口
    @PutMapping("/{orderNo}")
    public Order updateOrder(@PathVariable String orderNo, @RequestBody Order order) {
        return orderService.updateOrder(orderNo, order);
    }

    // 删除接口
    @DeleteMapping("/{orderNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String orderNo) {
        orderService.deleteOrder(orderNo);
    }

    // 更新订单状态接口
    @PatchMapping("/{orderNo}/status")
    public Order updateStatus(@PathVariable String orderNo, @RequestBody Map<String, String> body) {
        Order.OrderStatus status = Order.OrderStatus.valueOf(body.get("status"));
        return orderService.updateOrderStatus(orderNo, status);
    }
}
