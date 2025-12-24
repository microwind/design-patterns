package com.microwind.knife.interfaces.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 测试用的appCode和secretKey
    private static final String TEST_APP_CODE = "ios1";
    private static final String TEST_PATH = "/api/sign/submit-test";
    private static final String TEST_SECRET_KEY = "secret&!_caller1";

    // 存储测试过程中的动态盐值和签名信息
    private String dynamicSalt;
    private Long dynamicSaltTime;
    private String sign;
    private Long signTime;

    @Test
    @DisplayName("完整签名流程测试")
    void testCompleteSignProcess() throws Exception {
        // 步骤1：生成动态盐值
        generateDynamicSalt();
        
        // 步骤2：生成签名
        generateSign();
        
        // 步骤3：验证签名
        validateSign();
        
        // 步骤4：提交测试数据
        submitTestData();
    }

    /**
     * 测试动态盐值生成接口
     */
    void generateDynamicSalt() throws Exception {
        // 构建请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("appCode", TEST_APP_CODE);
        requestMap.put("path", TEST_PATH);
        
        String requestJson = objectMapper.writeValueAsString(requestMap);
        
        // 发送请求
        MvcResult result = mockMvc.perform(post("/api/sign/dynamic-salt-generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.isValid").value(true))
                .andExpect(jsonPath("$.data.dynamicSalt").isMap())
                .andExpect(jsonPath("$.data.dynamicSalt.dynamicSalt").isString())
                .andExpect(jsonPath("$.data.dynamicSalt.dynamicSaltTime").isNumber())
                .andReturn();
        
        // 解析响应数据
        Map<String, Object> responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
        Map<String, Object> dynamicSaltMap = (Map<String, Object>) dataMap.get("dynamicSalt");
        
        // 保存动态盐值信息
        this.dynamicSalt = (String) dynamicSaltMap.get("dynamicSalt");
        this.dynamicSaltTime = Long.valueOf(dynamicSaltMap.get("dynamicSaltTime").toString());
        
        // 验证动态盐值信息
        assertNotNull(this.dynamicSalt, "动态盐值不能为空");
        assertNotNull(this.dynamicSaltTime, "动态盐值时间不能为空");
    }

    /**
     * 测试签名生成接口
     */
    void generateSign() throws Exception {
        // 构建请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("appCode", TEST_APP_CODE);
        requestMap.put("path", TEST_PATH);
        requestMap.put("dynamicSalt", this.dynamicSalt);
        requestMap.put("dynamicSaltTime", this.dynamicSaltTime);
        
        String requestJson = objectMapper.writeValueAsString(requestMap);
        
        // 发送请求
        MvcResult result = mockMvc.perform(post("/api/sign/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sign").isString())
                .andExpect(jsonPath("$.data.time").isNumber())
                .andExpect(jsonPath("$.data.expireTime").isNumber())
                .andReturn();
        
        // 解析响应数据
        Map<String, Object> responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        Map<String, Object> dataMap = (Map<String, Object>) responseMap.get("data");
        
        // 保存签名信息
        this.sign = (String) dataMap.get("sign");
        this.signTime = Long.valueOf(dataMap.get("time").toString());
        
        // 验证签名信息
        assertNotNull(this.sign, "签名不能为空");
        assertNotNull(this.signTime, "签名时间不能为空");
    }

    /**
     * 测试签名验证接口
     */
    void validateSign() throws Exception {
        // 构建请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("appCode", TEST_APP_CODE);
        requestMap.put("path", TEST_PATH);
        requestMap.put("sign", this.sign);
        requestMap.put("time", this.signTime);
        
        String requestJson = objectMapper.writeValueAsString(requestMap);
        
        // 发送请求
        mockMvc.perform(post("/api/sign/sign-validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.isValid").value(true))
                .andExpect(jsonPath("$.data.sign").isMap())
                .andExpect(jsonPath("$.data.sign.sign").value(this.sign))
                .andExpect(jsonPath("$.data.sign.time").value(this.signTime));
    }

    /**
     * 测试带签名的数据提交接口
     */
    void submitTestData() throws Exception {
        // 构建请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("testData", "这是测试数据");
        requestMap.put("userId", 123456);
        
        String requestJson = objectMapper.writeValueAsString(requestMap);
        
        // 发送请求，在请求头中携带签名信息
        mockMvc.perform(post("/api/sign/submit-test")
                        .header("appCode", TEST_APP_CODE)
                        .header("sign", this.sign)
                        .header("time", this.signTime)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.isValid").value(true))
                .andExpect(jsonPath("$.data.params").isMap())
                .andExpect(jsonPath("$.data.params.testData").value("这是测试数据"))
                .andExpect(jsonPath("$.data.params.userId").value(123456));
    }

    /**
     * 测试无效签名的数据提交接口
     */
    @Test
    @DisplayName("无效签名的数据提交测试")
    void testSubmitTestWithInvalidSign() throws Exception {
        // 构建请求参数
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("testData", "这是测试数据");
        
        String requestJson = objectMapper.writeValueAsString(requestMap);
        
        // 使用无效的签名信息发送请求
        mockMvc.perform(post("/api/sign/submit-test")
                        .header("appCode", TEST_APP_CODE)
                        .header("sign", "invalid_sign")
                        .header("time", System.currentTimeMillis())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.data.isValid").value(false));
    }

    /**
     * 测试获取调用方全部api列表接口
     */
    @Test
    @DisplayName("获取调用方全部api列表测试")
    void testUserAuthList() throws Exception {
        // 首先生成动态盐值和签名
        generateDynamicSalt();
        generateSign();
        
        // 发送请求，在请求头中携带签名信息
        mockMvc.perform(post("/api/sign/user-auth-list")
                        .header("appCode", TEST_APP_CODE)
                        .header("sign", this.sign)
                        .header("path", "/api/sign/user-auth-list")
                        .header("time", this.signTime)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}