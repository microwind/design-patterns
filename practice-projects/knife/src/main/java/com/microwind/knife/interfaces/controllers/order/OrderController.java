package com.microwind.knife.interfaces.controllers.order;

import com.microwind.knife.application.dto.order.OrderPageDTO;
import com.microwind.knife.application.dto.order.OrderWithItemsPageDTO;
import com.microwind.knife.application.services.order.OrderService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.order.Order;
import com.microwind.knife.interfaces.vo.order.CreateOrderRequest;
import com.microwind.knife.interfaces.vo.order.UpdateOrderRequest;
import com.microwind.knife.interfaces.vo.order.UpdateOrderStatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    // 创建订单
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    // 查询全部订单接口
    @GetMapping("")
    public OrderPageDTO getAllOrders(Pageable pageable) {
        Page<Order> orderPages = orderService.getAllOrders(pageable);
        return new OrderPageDTO(orderPages);
    }

    // 查询全部订单接口（包含订单项）
    @GetMapping("/with-items")
    public OrderWithItemsPageDTO getAllOrdersWithItems(Pageable pageable) {
        Page<Order> orderPages = orderService.getAllOrdersWithItems(pageable);
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
//    @PutMapping("/{orderNo}")
    @RequestMapping(
            value = "/{orderNo}",
            method = {RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.POST}
    )
    public Order updateOrder(@PathVariable String orderNo, @RequestBody UpdateOrderRequest request) {
        return orderService.updateOrder(orderNo, request);
    }

    // 删除接口
    @DeleteMapping("/{orderNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String orderNo) {
        orderService.deleteOrder(orderNo);
    }

    // 更新订单状态接口
    @PatchMapping("/{orderNo}/status")
    public ApiResponse<UpdateOrderStatusResponse> updateStatus(@PathVariable String orderNo, @RequestBody UpdateOrderRequest request) {
        try {
            int result = orderService.updateOrderStatus(orderNo, request);
            if (result > 0) {
                UpdateOrderStatusResponse response = new UpdateOrderStatusResponse(orderNo, request.getStatus());
                return ApiResponse.success(response, "订单：" + orderNo + " 的状态更新为：" + request.getStatus());
            } else {
                return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "订单：" + orderNo + " 状态更新失败");
            }
        } catch (IllegalArgumentException ex) {
            return ApiResponse.failure(HttpStatus.BAD_REQUEST.value(), "无效的订单状态：" + ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        }
    }
}
