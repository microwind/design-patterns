package com.github.microwind.springboot4ddd.interfaces.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.microwind.springboot4ddd.application.dto.order.OrderDTO;
import com.github.microwind.springboot4ddd.application.service.order.OrderService;
import com.github.microwind.springboot4ddd.interfaces.vo.order.CreateOrderRequest;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderListResponse;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Order Controller API 单元测试 - CRUD 基本操作
 *
 * @author jarry
 * @since 1.0.0
 */
@DisplayName("Order Controller API 测试")
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService)).build();
    }

    // ==================== CREATE 测试 ====================

    @Test
    @DisplayName("创建订单 - 成功")
    void testCreateOrder_Success() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setTotalAmount(new BigDecimal("99.99"));

        OrderDTO orderDTO = OrderDTO.builder()
                .id(1L)
                .orderNo("ORD20250227001")
                .userId(1L)
                .totalAmount(new BigDecimal("99.99"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(orderService, times(1)).createOrder(any(CreateOrderRequest.class));
    }

    // ==================== READ 测试 ====================

    @Test
    @DisplayName("获取订单详情 - 成功")
    void testGetOrderDetail_Success() throws Exception {
        long orderId = 1L;
        OrderResponse response = OrderResponse.builder()
                .id(orderId)
                .orderNo("ORD20250227001")
                .userId(1L)
                .totalAmount(new BigDecimal("99.99"))
                .status("PENDING")
                .statusDesc("待支付")
                .createdAt(LocalDateTime.now())
                .build();

        when(orderService.getOrderDetail(orderId)).thenReturn(response);

        mockMvc.perform(get("/api/orders/{id}", orderId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        verify(orderService, times(1)).getOrderDetail(orderId);
    }

    @Test
    @DisplayName("获取用户订单列表 - 成功")
    void testGetUserOrderList_Success() throws Exception {
        long userId = 1L;
        List<OrderListResponse> orders = Arrays.asList(
                OrderListResponse.builder()
                        .order(OrderResponse.builder()
                                .id(1L)
                                .orderNo("ORD20250227001")
                                .userId(userId)
                                .totalAmount(new BigDecimal("99.99"))
                                .status("PENDING")
                                .build())
                        .userName("testuser")
                        .userPhone("13800138000")
                        .build()
        );

        when(orderService.getUserOrderList(userId)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/user/{userId}", userId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(orderService, times(1)).getUserOrderList(userId);
    }

    @Test
    @DisplayName("获取所有订单列表 - 成功")
    void testGetAllOrderList_Success() throws Exception {
        List<OrderListResponse> orders = Arrays.asList(
                OrderListResponse.builder()
                        .order(OrderResponse.builder()
                                .id(1L)
                                .orderNo("ORD20250227001")
                                .userId(1L)
                                .totalAmount(new BigDecimal("99.99"))
                                .status("PENDING")
                                .build())
                        .userName("user1")
                        .userPhone("13800138000")
                        .build(),
                OrderListResponse.builder()
                        .order(OrderResponse.builder()
                                .id(2L)
                                .orderNo("ORD20250227002")
                                .userId(2L)
                                .totalAmount(new BigDecimal("199.99"))
                                .status("PAID")
                                .build())
                        .userName("user2")
                        .userPhone("13900139000")
                        .build()
        );

        when(orderService.getAllOrderList()).thenReturn(orders);

        mockMvc.perform(get("/api/orders/list")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(orderService, times(1)).getAllOrderList();
    }

    // ==================== UPDATE 测试 - 订单状态变更 ====================

    @Test
    @DisplayName("支付订单 - 成功")
    void testPayOrder_Success() throws Exception {
        long orderId = 1L;
        OrderDTO orderDTO = OrderDTO.builder()
                .id(orderId)
                .orderNo("ORD20250227001")
                .userId(1L)
                .totalAmount(new BigDecimal("99.99"))
                .status("PAID")
                .build();

        when(orderService.payOrder(orderId)).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders/{id}/pay", orderId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("PAID"));

        verify(orderService, times(1)).payOrder(orderId);
    }

    @Test
    @DisplayName("取消订单 - 成功")
    void testCancelOrder_Success() throws Exception {
        long orderId = 1L;
        OrderDTO orderDTO = OrderDTO.builder()
                .id(orderId)
                .orderNo("ORD20250227001")
                .userId(1L)
                .totalAmount(new BigDecimal("99.99"))
                .status("CANCELLED")
                .build();

        when(orderService.cancelOrder(orderId)).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders/{id}/cancel", orderId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    @DisplayName("完成订单 - 成功")
    void testCompleteOrder_Success() throws Exception {
        long orderId = 1L;
        OrderDTO orderDTO = OrderDTO.builder()
                .id(orderId)
                .orderNo("ORD20250227001")
                .userId(1L)
                .totalAmount(new BigDecimal("99.99"))
                .status("COMPLETED")
                .build();

        when(orderService.completeOrder(orderId)).thenReturn(orderDTO);

        mockMvc.perform(post("/api/orders/{id}/complete", orderId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        verify(orderService, times(1)).completeOrder(orderId);
    }

    // ==================== DELETE 测试 ====================

    @Test
    @DisplayName("删除订单 - 成功")
    void testDeleteOrder_Success() throws Exception {
        long orderId = 1L;
        doNothing().when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/api/orders/{id}", orderId)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(orderService, times(1)).deleteOrder(orderId);
    }
}
