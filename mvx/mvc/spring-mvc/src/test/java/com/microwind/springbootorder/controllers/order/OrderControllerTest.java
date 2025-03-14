package com.microwind.springbootorder.controllers.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // 自动配置 MockMvc 用于模拟 HTTP 请求
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 用于保存创建订单时返回的 orderNo
    private String orderNo;

    @Test
    @BeforeEach
    @DisplayName("创建订单")
    void testCreateOrder() throws Exception {
        String requestJson = objectMapper.writeValueAsString(Map.of(
                "amount", 99.99,
                "orderName", "Test Order1",
                "userId", 110110
        ));

        MvcResult result = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").isMap())  // 确保返回的 data 是一个 Map
                .andReturn();

        // 提取返回的 orderNo（假设响应中包含 orderNo）
        this.orderNo = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("orderNo").asText();
    }

    @Test
    @DisplayName("查询订单")
    void testGetOrder() throws Exception {
        // 使用创建订单时返回的 orderNo
        mockMvc.perform(get("/api/orders/" + orderNo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))  // 响应代码为 200
                .andExpect(jsonPath("$.data.orderNo").value(orderNo));  // 验证 orderNo
    }

    @Test
    @DisplayName("查询全部订单")
    void testGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))  // 响应代码为 200
                .andExpect(jsonPath("$.data").isMap());  // 确保返回的是一个Map
    }

    @Test
    @DisplayName("更新订单")
    void testUpdateOrder() throws Exception {
        // 更新订单，使用创建订单时返回的 orderNo
        String updateJson = objectMapper.writeValueAsString(Map.of(
                "amount", 11.22,
                "orderName", "Test Order2"
        ));

        mockMvc.perform(put("/api/orders/" + orderNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))  // 响应代码为 200
                .andExpect(jsonPath("$.data.amount").value(11.22));  // 验证更新后的金额
    }

    @Test
    @DisplayName("删除订单")
    void testDeleteOrder() throws Exception {
        // 删除订单，使用创建订单时返回的 orderNo
        mockMvc.perform(delete("/api/orders/" + orderNo))
                .andExpect(status().isNoContent());

        // 验证订单已删除
        mockMvc.perform(get("/api/orders/" + orderNo))
                .andExpect(status().isNotFound());
    }
}
