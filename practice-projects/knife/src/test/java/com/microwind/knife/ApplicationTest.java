package com.microwind.knife;

import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString; // 导入 containsString 方法

@SpringBootTest(classes = Application.class) // 启动类 Application 是你的主应用类
@AutoConfigureMockMvc // 自动配置 MockMvc 用于模拟 HTTP 请求
public class ApplicationTest {

    @Autowired
    private MockMvc mockMvc;  // 自动注入 MockMvc

    @Test
    public void testHomepage() throws Exception {
        // 模拟访问根路径
        mockMvc.perform(get("/").accept(MediaType.ALL))
                .andExpect(status().isOk()) // 验证状态码是 200
                .andExpect(content().string(containsString("Welcome"))); // 验证返回的内容包含字符 'Welcome'
    }
}
