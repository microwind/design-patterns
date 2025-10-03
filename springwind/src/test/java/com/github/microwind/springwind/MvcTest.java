package com.github.microwind.springwind;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.core.SpringWindApplicationContext;
import com.github.microwind.springwind.web.DispatcherServlet;
import com.github.microwind.springwind.web.HandlerMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletConfig;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MvcTest {

    // 1. 测试用Controller（必须加@Controller，确保被扫描）
    @Controller
    @RequestMapping("/user") // 类级别路径：/user
    public static class TestUserController {
        // 方法1：GET /user/info → 转发到userInfo.jsp
        @RequestMapping(value = "/info", method = "GET")
        public String getUserInfo() {
            return "userInfo";
        }

        // 方法2：POST /user/logout → 重定向到/login
        @RequestMapping(value = "/logout", method = "POST")
        public String logout() {
            return "redirect:/login";
        }

        // 方法3：GET /user/data → 返回JSON
        @RequestMapping(value = "/data", method = "GET")
        public Map<String, Object> getUserData() {
            Map<String, Object> data = new HashMap<>();
            data.put("id", 1L);
            data.put("name", "test");
            return data;
        }
    }

    private DispatcherServlet dispatcherServlet;

    @Mock
    private ServletConfig servletConfig;

    private SpringWindApplicationContext applicationContext;

    @Before
    public void init() throws Exception {
        // 2. 初始化容器：传入MvcTest.class（包为com.github.microwind.springwind，扫描正确包）
        applicationContext = new SpringWindApplicationContext(MvcTest.class);

        // 3. 模拟ServletConfig（避免依赖web.xml配置）
        when(servletConfig.getInitParameter("configClass")).thenReturn(MvcTest.class.getName());

        // 4. 初始化DispatcherServlet
        dispatcherServlet = new DispatcherServlet();
        // 手动注入applicationContext（因测试环境无ServletContext自动注入）
        setPrivateField(dispatcherServlet, "applicationContext", applicationContext);
        dispatcherServlet.init(servletConfig);
    }

    // 测试1：转发视图（GET /user/info）
    @Test
    public void testService_Forward() throws Exception {
        // 1. 创建Mock请求，指定HTTP方法为GET，URI可随意（但建议与ServletPath一致）
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/info");
        // 2. 关键：手动设置ServletPath，与HandlerMapping的路径一致（/user/info）
        request.setServletPath("/user/info");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        // 断言：状态码200，转发路径正确
        assertEquals(200, response.getStatus());
        // 验证转发路径（通过request的转发属性获取，而非直接比较RequestDispatcher对象）
        String forwardedPath = (String) request.getAttribute("javax.servlet.forward.servlet_path");
        assertEquals("/WEB-INF/views/userInfo.jsp", forwardedPath);
    }

    // 测试2：重定向（POST /user/logout）
    @Test
    public void testService_Redirect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user/logout");
        request.setServletPath("/user/logout"); // 手动设置ServletPath
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        // 断言：状态码302，重定向路径正确
        assertEquals(302, response.getStatus());
        assertEquals("/login", response.getRedirectedUrl());
    }

    // 测试3：返回JSON（GET /user/data）
    @Test
    public void testService_ReturnMap() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/data");
        request.setServletPath("/user/data"); // 手动设置ServletPath
        MockHttpServletResponse response = new MockHttpServletResponse();

//        dispatcherServlet.service(request, response);

        // 断言：状态码200，Content-Type为application/json
        assertEquals(200, response.getStatus());
        assertEquals("application/json;charset=UTF-8", response.getContentType());
    }

    // 测试4：HandlerMapping初始化（验证映射是否生成）
    @Test
    public void testInitHandlerMappings() throws Exception {
        // 获取dispatcherServlet中的handlerMappings（私有属性）
        Map<String, HandlerMapping> handlerMappings = (Map<String, HandlerMapping>) getPrivateField(dispatcherServlet, "handlerMappings");

        // 验证映射是否正确生成（HTTP方法:完整URL）
        assertTrue(handlerMappings.containsKey("GET:/user/info"));
        assertTrue(handlerMappings.containsKey("POST:/user/logout"));
        assertTrue(handlerMappings.containsKey("GET:/user/data"));
    }

    @After
    public void clean() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    // 工具方法：反射获取私有属性
    private Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    // 工具方法：反射设置私有属性
    private void setPrivateField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}