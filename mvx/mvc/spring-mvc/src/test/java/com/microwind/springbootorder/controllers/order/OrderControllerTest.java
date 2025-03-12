package com.microwind.springbootorder.controllers.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Map;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Jackson 序列化

    @Test
    @DisplayName("创建订单")
    void testCreateOrder() throws Exception {
        String requestJson = objectMapper.writeValueAsString(Map.of(
                "customerName", "齐天大圣",
                "amount", 99.99
        ));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.customerName").value("齐天大圣"))
                .andExpect((ResultMatcher) jsonPath("$.amount").value(99.99));
    }

    @Test
    @DisplayName("查询订单")
    void testGetOrder() throws Exception {
        String orderId = "123456"; // 替换为实际订单号

        mockMvc.perform(get("/api/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value(orderId));
    }

    @Test
    @DisplayName("更新订单")
    void testUpdateOrder() throws Exception {
        String orderId = "123456";
        String updateJson = objectMapper.writeValueAsString(Map.of(
                "customerName", "孙悟空",
                "amount", 11.22
        ));

        mockMvc.perform(put("/api/orders/" + orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.customerName").value("孙悟空"))
                .andExpect((ResultMatcher) jsonPath("$.amount").value(11.22));
    }

    @Test
    @DisplayName("删除订单")
    void testDeleteOrder() throws Exception {
        String orderId = "123456";

        mockMvc.perform(delete("/api/orders/" + orderId))
                .andExpect(status().isOk());
    }
}
