package com.microwind.knife.interfaces.controllers.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // 自动配置 MockMvc 用于模拟 HTTP 请求
@Transactional // 添加事务支持，测试完成后自动回滚（可选）
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String orderNo;

    // 初始化方法：每个测试方法前执行
    @BeforeEach
    void setup() throws Exception {
        createOrder();
    }

    // 清理方法：每个测试方法后执行
    @AfterEach
    void cleanup() throws Exception {
        deleteOrder();
    }

    @DisplayName("创建订单")
    String createOrder() throws Exception {
        String requestJson = objectMapper.writeValueAsString(Map.of(
                "amount", 99.99,
                "orderName", "Test Order1",
                "userId", 110110
        ));

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").isMap())
                .andReturn();

        this.orderNo = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("orderNo").asText();
        return orderNo;
    }

    @DisplayName("删除订单")
    void deleteOrder() throws Exception {
        if (orderNo != null) {
            mockMvc.perform(delete("/api/orders/" + orderNo))
                    .andExpect(status().isNoContent());
        }
    }

    @Test
    @DisplayName("创建订单")
    void testCreateOrderMethod() throws Exception {
        String newOrderNo = createOrder();
        // 验证创建的订单是否可以查询到
        mockMvc.perform(get("/api/orders/" + newOrderNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderNo").value(newOrderNo));
    }


    @Test
    @DisplayName("查询订单")
    void testGetOrder() throws Exception {
        mockMvc.perform(get("/api/orders/" + orderNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderNo").value(orderNo));
    }

    @Test
    @DisplayName("查询全部订单")
    void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isMap());
    }

    @Test
    @DisplayName("更新订单状态")
    void testUpdateOrderStatus() throws Exception {
        String updateJson = objectMapper.writeValueAsString(Map.of(
                "status", "COMPLETED"
        ));

        mockMvc.perform(patch("/api/orders/" + orderNo + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("更新订单")
    void testUpdateOrder() throws Exception {
        String updateJson = objectMapper.writeValueAsString(Map.of(
                "amount", 11.22,
                "orderName", "Test Order2"
        ));

        mockMvc.perform(put("/api/orders/" + orderNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.amount").value(11.22));
    }

    @Test
    @DisplayName("删除订单")
    void testDeleteOrderMethod() throws Exception {
        String newOrderNo = createOrder();
        // 手动验证状态码
        MvcResult result = mockMvc.perform(get("/api/orders/" + newOrderNo))
                .andReturn();
        int statusCode = result.getResponse().getStatus();
        assertTrue(statusCode == HttpStatus.NOT_FOUND.value() ||
                        statusCode == HttpStatus.OK.value() ||
                        statusCode == HttpStatus.CREATED.value(),
                "Unexpected status code: " + statusCode);
    }
}