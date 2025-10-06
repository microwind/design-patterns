package com.github.microwind.springwind;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.core.SpringWindApplicationContext;
import com.github.microwind.springwind.web.DispatcherServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import com.github.microwind.springwind.mock.MockHttpServletRequest;
import com.github.microwind.springwind.mock.MockHttpServletResponse;

import javax.servlet.ServletConfig;
import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * DispatcherServlet转发功能专项测试
 * 专注于测试请求转发、重定向、视图解析等核心功能
 */
@RunWith(MockitoJUnitRunner.class)
public class DispatcherServletTest {

    @Controller
    @RequestMapping("/view")
    public static class ViewController {

        // 返回视图名称，应该被解析为JSP路径
        @RequestMapping(value = "/info", method = "GET")
        public String getInfo() {
            return "userInfo";
        }

        // 直接返回JSP路径
        @RequestMapping(value = "/profile", method = "GET")
        public String getProfile() {
            return "/WEB-INF/views/profile.jsp";
        }

        // 重定向到外部URL
        @RequestMapping(value = "/external", method = "GET")
        public String externalRedirect() {
            return "redirect:https://example.com";
        }

        // 重定向到内部路径
        @RequestMapping(value = "/internal", method = "GET")
        public String internalRedirect() {
            return "redirect:/view/info";
        }

        // 链式转发
        @RequestMapping(value = "/chain", method = "GET")
        public String chainForward() {
            return "forward:/view/profile";
        }
    }

    private DispatcherServlet dispatcherServlet;

    @Mock
    private ServletConfig servletConfig;

    private SpringWindApplicationContext applicationContext;

    @Before
    public void init() throws Exception {
        applicationContext = new SpringWindApplicationContext(DispatcherServletTest.class);
        when(servletConfig.getInitParameter("configClass")).thenReturn(DispatcherServletTest.class.getName());

        dispatcherServlet = new DispatcherServlet();
        setPrivateField(dispatcherServlet, "applicationContext", applicationContext);
        dispatcherServlet.init(servletConfig);
    }

    // 测试1：视图名称解析为完整JSP路径
    @Test
    public void testViewNameResolution() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/view/info");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(200, response.getStatus());
        // 验证视图名称被正确解析为完整JSP路径
        assertEquals("/WEB-INF/views/userInfo.jsp", response.getForwardedUrl());
    }

    // 测试2：直接JSP路径转发
    @Test
    public void testDirectJspPath() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/view/profile");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(200, response.getStatus());
        // 验证直接JSP路径被正确转发
        assertEquals("/WEB-INF/views/profile.jsp", response.getForwardedUrl());
    }

    // 测试3：外部URL重定向
    @Test
    public void testExternalRedirect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/view/external");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(302, response.getStatus());
        assertEquals("https://example.com", response.getRedirectedUrl());
    }

    // 测试4：内部路径重定向
    @Test
    public void testInternalRedirect() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/view/internal");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(302, response.getStatus());
        assertEquals("/view/info", response.getRedirectedUrl());
    }

    // 测试5：链式转发
    @Test
    public void testChainForward() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/view/chain");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(200, response.getStatus());
        assertEquals("/view/profile", response.getForwardedUrl());
    }

    // 测试6：404未找到
    @Test
    public void testNotFound() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/view/nonexistent");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        assertEquals(404, response.getStatus());
    }

    // 测试7：方法不匹配
    @Test
    public void testMethodNotAllowed() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/view/info");
        MockHttpServletResponse response = new MockHttpServletResponse();

        dispatcherServlet.service(request, response);

        // 根据实现，可能是404或405
        assertTrue("Should be 404 or 405",
                response.getStatus() == 404 || response.getStatus() == 405);
    }

    // 测试8：视图解析器配置验证，不支持viewResolver模式
//    @Test
//    public void testViewResolverConfiguration() throws Exception {
//        // 验证视图解析器是否正确配置了前缀和后缀
//        Object viewResolver = getPrivateField(dispatcherServlet, "viewResolver");
//        if (viewResolver != null) {
//            String prefix = (String) getPrivateField(viewResolver, "prefix");
//            String suffix = (String) getPrivateField(viewResolver, "suffix");
//
//            assertEquals("/WEB-INF/views/", prefix);
//            assertEquals(".jsp", suffix);
//        }
//    }

    // 测试9：多次转发场景
    @Test
    public void testMultipleForwards() throws Exception {
        // 模拟多次请求，验证转发状态能够正确重置
        MockHttpServletRequest request1 = new MockHttpServletRequest("GET", "/view/info");
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        dispatcherServlet.service(request1, response1);
        assertEquals("/WEB-INF/views/userInfo.jsp", response1.getForwardedUrl());

        MockHttpServletRequest request2 = new MockHttpServletRequest("GET", "/view/profile");
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        dispatcherServlet.service(request2, response2);
        assertEquals("/WEB-INF/views/profile.jsp", response2.getForwardedUrl());
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