package com.github.microwind.springwind;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.RequestParam;
import com.github.microwind.springwind.core.SpringWindApplicationContext;
import com.github.microwind.springwind.web.DispatcherServlet;
import com.github.microwind.springwind.web.HandlerMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import com.github.microwind.springwind.mock.MockHttpServletRequest;
import com.github.microwind.springwind.mock.MockHttpServletResponse;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * 全面的MVC测试类，验证整体流程，包括拦截器、参数处理、返回值处理等
 */
@RunWith(MockitoJUnitRunner.class)
public class MvcTest {

    @Controller
    @RequestMapping("/api")
    public static class ApiController {

        // 测试参数绑定
        @RequestMapping(value = "/user", method = "GET")
        public String getUser(@RequestParam("id") Long id, @RequestParam("name") String name) {
            return "user:" + id + ":" + name;
        }

        // 测试Map返回值自动转为JSON
        @RequestMapping(value = "/data", method = "GET")
        public Map<String, Object> getData() {
            Map<String, Object> data = new HashMap<>();
            data.put("status", "success");
            data.put("code", 200);
            data.put("message", "操作成功");
            return data;
        }

        // 测试重定向
        @RequestMapping(value = "/redirect", method = "GET")
        public String redirect() {
            return "redirect:/login";
        }

        // 测试转发
        @RequestMapping(value = "/forward", method = "GET")
        public String forward() {
            return "forward:/home";
        }

        // 测试异常处理
        @RequestMapping(value = "/error", method = "GET")
        public String error() {
            throw new RuntimeException("测试异常");
        }
    }

    private DispatcherServlet dispatcherServlet;

    @Mock
    private ServletConfig servletConfig;

    private SpringWindApplicationContext applicationContext;

    @Before
    public void init() throws Exception {
        applicationContext = new SpringWindApplicationContext(MvcTest.class);
        when(servletConfig.getInitParameter("configClass")).thenReturn(MvcTest.class.getName());

        dispatcherServlet = new DispatcherServlet();
        setPrivateField(dispatcherServlet, "applicationContext", applicationContext);
        dispatcherServlet.init(servletConfig);
    }

    // 测试1：参数绑定功能
    @Test
    public void testParameterBinding() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        request.setServletPath("/api/user");
        request.addParameter("id", "123");
        request.addParameter("name", "张三");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(200, response.getStatus());
        String content = response.getContentAsString();
        assertTrue(content.contains("user:123:张三"));
    }

    // 测试2：JSON返回值处理
    @Test
    public void testJsonResponse() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/data");
        request.setServletPath("/api/data");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(200, response.getStatus());
        assertEquals("application/json;charset=UTF-8", response.getContentType());

        String content = response.getContentAsString();
        assertTrue(content.contains("\"status\":\"success\""));
        assertTrue(content.contains("\"code\":200"));
        assertTrue(content.contains("\"message\":\"操作成功\""));
    }

    // 测试3：重定向功能
    @Test
    public void testRedirect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/redirect");
        request.setServletPath("/api/redirect");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(302, response.getStatus());
        assertEquals("/login", response.getRedirectedUrl());
    }

    // 测试4：转发功能
    @Test
    public void testForward() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/forward");
        request.setServletPath("/api/forward");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(200, response.getStatus());
        assertEquals("/home", response.getForwardedUrl());
    }

    // 测试5：异常处理
    @Test
    public void testExceptionHandling() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/error");
        request.setServletPath("/api/error");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 此时service方法不会抛出异常，流程正常执行
        dispatcherServlet.service(request, response);

        // 断言1：状态码为500（服务器内部错误）
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
        // 断言2：响应体包含错误信息（可选，更严谨）
        String errorContent = response.getContentAsString();
        assertTrue(errorContent.contains("测试异常"));
    }

    // 测试6：HandlerMapping初始化
    @Test
    public void testHandlerMappings() throws Exception {
        Map<String, HandlerMapping> handlerMappings = (Map<String, HandlerMapping>)
                getPrivateField(dispatcherServlet, "handlerMappings");

        assertNotNull("Handler mappings should not be null", handlerMappings);
        assertTrue("Should contain GET:/api/user", handlerMappings.containsKey("GET:/api/user"));
        assertTrue("Should contain GET:/api/data", handlerMappings.containsKey("GET:/api/data"));
        assertTrue("Should contain GET:/api/redirect", handlerMappings.containsKey("GET:/api/redirect"));
        assertTrue("Should contain GET:/api/forward", handlerMappings.containsKey("GET:/api/forward"));
        assertTrue("Should contain GET:/api/error", handlerMappings.containsKey("GET:/api/error"));
    }

    // 测试7：内容协商
    @Test
    public void testContentNegotiation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/data");
        request.setServletPath("/api/data");
        request.addHeader("Accept", "application/json");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(200, response.getStatus());
        assertTrue(response.getContentType().contains("application/json"));
    }

    @After
    public void clean() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}