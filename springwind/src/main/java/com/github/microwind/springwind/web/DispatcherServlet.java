package com.github.microwind.springwind.web;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.core.SpringWindApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * SpringWind DispatcherServlet - 前端控制器
 * 负责处理所有HTTP请求，将请求分发给对应的Controller处理
 */
public class DispatcherServlet extends HttpServlet {
    private final Map<String, HandlerMapping> handlerMappings = new HashMap<>();
    private SpringWindApplicationContext applicationContext;

    /**
     * 初始化Spring容器和Handler映射
     */
    @Override
    public void init() throws ServletException {
        try {
            // 1. 获取配置类的全限定名（从ServletConfig中获取，如"com.github.microwind.springwind.MvcTest"）
            String configClassName = getInitParameter("configClass");
            if (configClassName == null || configClassName.trim().isEmpty()) {
                throw new ServletException("未配置configClass参数（需指定Spring配置类的全限定名）");
            }

            // 2. 将字符串类名转换为Class对象（避免使用String.getClass()）
            Class<?> configClass = Class.forName(configClassName);

            // 3. 初始化Spring容器（此时容器会扫描configClass所在包下的@Controller）
            applicationContext = new SpringWindApplicationContext(configClass);

            // 4. 初始化HandlerMapping（此时能找到@Controller，生成映射）
            initHandlerMappings();

        } catch (ClassNotFoundException e) {
            throw new ServletException("找不到配置类：" + getInitParameter("configClass"), e);
        } catch (Exception e) {
            throw new ServletException("DispatcherServlet初始化失败", e);
        }
    }

    /**
     * 初始化Handler映射
     * 扫描所有Controller Bean，根据@RequestMapping注解构建Handler映射
     */
    private void initHandlerMappings() {
        // 获取所有@Controller注解的Bean
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);
        if (controllers.isEmpty()) {
            throw new RuntimeException("未扫描到任何@Controller注解的Bean，请检查包扫描范围");
        }

        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            Object controller = entry.getValue();
            Class<?> clazz = controller.getClass();

            // 1. 解析类级别@RequestMapping（拼接基础路径）
            String basePath = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                basePath = clazz.getAnnotation(RequestMapping.class).value();
                // 处理路径格式（避免重复"/"，如basePath为""时不拼接）
                if (!basePath.isEmpty() && !basePath.startsWith("/")) {
                    basePath = "/" + basePath;
                }
            }

            // 2. 解析方法级别@RequestMapping（使用getDeclaredMethods()获取当前类方法）
            for (Method method : clazz.getDeclaredMethods()) { // 替换为getDeclaredMethods()
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                    // 处理方法路径格式
                    String methodPath = requestMapping.value();
                    if (methodPath.isEmpty() || !methodPath.startsWith("/")) {
                        methodPath = "/" + methodPath;
                    }

                    // 拼接完整路径（类路径 + 方法路径）
                    String fullPath = basePath + methodPath;
                    // 获取HTTP方法（如"GET"、"POST"）
                    String httpMethod = requestMapping.method().toUpperCase(); // 统一大写，避免大小写问题

                    // 生成映射Key（格式：HTTP方法:完整路径）
                    String mappingKey = httpMethod + ":" + fullPath;
                    // 存储映射（避免重复Key覆盖）
                    if (handlerMappings.containsKey(mappingKey)) {
                        throw new RuntimeException("存在重复的请求映射：" + mappingKey);
                    }
                    handlerMappings.put(mappingKey, new HandlerMapping(controller, method));
                    System.out.println("生成请求映射：" + mappingKey); // 调试用，确认映射生成
                }
            }
        }
    }

    private String getString(Method method, String basePath) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

        // 处理方法路径格式（如"/info"）
        String methodPath = requestMapping.value();
        if (methodPath.isEmpty() || !methodPath.startsWith("/")) {
            methodPath = "/" + methodPath;
        }

        // 拼接完整路径（类路径 + 方法路径，如"/user/info"）
        String fullPath = basePath + methodPath;
        // 获取HTTP方法（如"GET"、"POST"）
        String httpMethod = requestMapping.method().toUpperCase(); // 统一大写，避免大小写问题

        // 生成映射Key（格式：HTTP方法:完整路径，如"GET:/user/info"）
        String mappingKey = httpMethod + ":" + fullPath;
        // 存储映射（避免重复Key覆盖）
        if (handlerMappings.containsKey(mappingKey)) {
            throw new RuntimeException("存在重复的请求映射：" + mappingKey);
        }
        return mappingKey;
    }


    /**
     * 处理HTTP请求
     * 根据请求路径和HTTP方法查找对应的HandlerMapping，调用处理方法
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 关键修复：用getServletPath()代替getRequestURI()，避免上下文路径干扰
        String path = req.getServletPath();
        String httpMethod = req.getMethod().toUpperCase(); // 统一大写，与映射Key一致
        String key = httpMethod + ":" + path;
        System.out.println("当前请求Key：" + key); // 调试用，确认请求Key与映射Key匹配

        // 查找对应的HandlerMapping
        HandlerMapping handlerMapping = handlerMappings.get(key);
        if (handlerMapping != null) {
            try {
                // 调用处理方法（注意：Controller方法参数需与invoke参数匹配）
                // 若Controller方法无参数（如getUserInfo()），则invoke第二个参数传null或空数组
                Object result;
                Method targetMethod = handlerMapping.getMethod();
                Class<?>[] paramTypes = targetMethod.getParameterTypes();
                if (paramTypes.length == 0) {
                    result = targetMethod.invoke(handlerMapping.getController());
                } else {
                    // 若方法有参数（如(HttpServletRequest, HttpServletResponse)），则传入req和resp
                    result = targetMethod.invoke(handlerMapping.getController(), req, resp);
                }

                // 处理返回结果
                handleResult(result, req, resp);
            } catch (Exception e) {
                throw new ServletException("处理请求[" + key + "]失败", e);
            }
        } else {
            resp.setStatus(404);
            resp.getWriter().write("Not Found: " + key); // 输出未匹配的Key，便于调试
        }
    }

    /**
     * 处理返回结果
     * 处理Controller方法返回的结果，根据不同类型进行不同的处理
     * 1. 如果返回值是String类型，根据是否以"redirect:"开头判断是否重定向或转发到JSP视图
     * 2. 如果返回值是Map类型，假设是JSON数据，设置响应类型为application/json
     *    并将Map转换为JSON字符串写入响应体
     * @param result Controller方法返回的结果
     * @param req    HttpServletRequest对象
     * @param resp   HttpServletResponse对象
     */
    private void handleResult(Object result, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        resp.setCharacterEncoding("UTF-8"); // 避免中文乱码
        if (result instanceof String) {
            String viewName = (String) result;
            if (viewName.startsWith("redirect:")) {
                // 重定向：去掉"redirect:"前缀
                String redirectUrl = viewName.substring("redirect:".length());
                resp.sendRedirect(redirectUrl);
            } else {
                // 转发到JSP：路径格式为/WEB-INF/views/xxx.jsp
                String jspPath = "/WEB-INF/views/" + viewName + ".jsp";
                req.getRequestDispatcher(jspPath).forward(req, resp);
            }
        } else if (result instanceof Map) {
            // 处理JSON响应（模拟JSON序列化，实际项目用JSON库）
            resp.setContentType("application/json");
            Map<?, ?> dataMap = (Map<?, ?>) result;
            StringBuilder json = new StringBuilder("{");
            for (Map.Entry<?, ?> entry : dataMap.entrySet()) {
                json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
            }
            if (json.length() > 1) {
                json.deleteCharAt(json.length() - 1); // 去掉最后一个逗号
            }
            json.append("}");
            resp.getWriter().write(json.toString());
        }
    }

    /**
     * 销毁Servlet，关闭Spring容器
     */
    @Override
    public void destroy() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }
}