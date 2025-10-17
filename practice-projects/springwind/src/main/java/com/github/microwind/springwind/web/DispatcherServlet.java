package com.github.microwind.springwind.web;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.RequestParam;
import com.github.microwind.springwind.core.SpringWindApplicationContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
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
            // 1. 获取配置类的全限定名（从ServletConfig中获取）
            String configClassName = getInitParameter("configClass");
            if (configClassName == null || configClassName.trim().isEmpty()) {
                throw new ServletException("未配置configClass参数（需指定Spring配置类的全限定名）");
            }

            // 2. 将字符串类名转换为Class对象（关键：避免之前的String.class错误）
            Class<?> configClass = Class.forName(configClassName);

            // 3. 初始化Spring容器（扫描configClass所在包下的@Controller）
            applicationContext = new SpringWindApplicationContext(configClass);

            // 4. 初始化HandlerMapping（生成请求映射）
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
                basePath = normalizePath(clazz.getAnnotation(RequestMapping.class).value());
            }

            // 2. 解析方法级别@RequestMapping（仅处理当前类及父类的public方法，带@RequestMapping的才生成映射）
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                    // 处理方法路径格式（规范化）
                    String methodPath = normalizePath(requestMapping.value());

                    // 拼接完整路径（类路径 + 方法路径，避免双斜杠）
                    String fullPath = combinePaths(basePath, methodPath);

                    // 生成映射Key（HTTP方法:完整路径，统一大写）
                    String mappingKey = getMappingKey(requestMapping, fullPath);
                    handlerMappings.put(mappingKey, new HandlerMapping(controller, method));
                    System.out.println("生成请求映射：" + mappingKey); // 调试日志：确认映射生成
                }
            }
        }
    }

    /**
     * 生成请求映射Key（格式：HTTP方法:完整路径）
     * 处理默认HTTP方法（未指定时默认GET），避免重复映射
     */
    private String getMappingKey(RequestMapping requestMapping, String fullPath) {
        String httpMethod = requestMapping.method().trim();
        // 未指定HTTP方法时，默认GET
        if (httpMethod.isEmpty()) {
            httpMethod = "GET";
        }
        httpMethod = httpMethod.toUpperCase(); // 统一大写，避免大小写不匹配

        // 生成映射Key
        String mappingKey = httpMethod + ":" + fullPath;

        // 避免重复映射（重复时抛异常，提前发现问题）
        if (handlerMappings.containsKey(mappingKey)) {
            throw new RuntimeException("存在重复的请求映射：" + mappingKey);
        }
        return mappingKey;
    }

    /**
     * 规范化路径：确保以单个斜杠开头，无尾部斜杠（根路径除外）
     */
    private String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }
        path = path.trim();
        // 确保以单个斜杠开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        // 去除尾部斜杠（根路径"/"除外）
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 合并两个路径：处理中间可能的双斜杠问题（如"/user" + "/info" → "/user/info"）
     */
    private String combinePaths(String path1, String path2) {
        if (path1.isEmpty()) {
            return path2;
        }
        if (path2.isEmpty()) {
            return path1;
        }

        // 处理中间斜杠：避免双斜杠或无斜杠
        if (path1.endsWith("/") && path2.startsWith("/")) {
            return path1 + path2.substring(1); // 去掉path2的开头斜杠
        } else if (!path1.endsWith("/") && !path2.startsWith("/")) {
            return path1 + "/" + path2; // 中间加斜杠
        } else {
            return path1 + path2; // 已有单个斜杠，直接拼接
        }
    }

    /**
     * 处理HTTP请求：匹配HandlerMapping → 解析参数 → 调用Controller → 处理结果
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 获取请求路径（优先使用 getPathInfo，如果为null则使用 getServletPath）
        String path = req.getPathInfo();
        if (path == null || path.isEmpty()) {
            path = req.getServletPath();
        }
        if (path == null || path.isEmpty()) {
            path = req.getRequestURI();
            String contextPath = req.getContextPath();
            if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
                path = path.substring(contextPath.length());
            }
        }
        String httpMethod = req.getMethod().toUpperCase();
        String requestKey = httpMethod + ":" + path;
        System.out.println("当前请求Key：" + requestKey); // 调试日志：确认请求Key与映射匹配

        // 查找对应的HandlerMapping
        HandlerMapping handlerMapping = handlerMappings.get(requestKey);
        if (handlerMapping != null) {
            try {
                // 1. 解析方法参数（无参方法返回空数组，避免参数不匹配）
                Object[] methodArgs = resolveMethodParameters(handlerMapping.getMethod(), req, resp);
                System.out.println("解析到的参数数量：" + methodArgs.length); // 调试日志：确认无参方法返回0

                // 2. 反射调用Controller方法（获取返回结果）
                Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), methodArgs);
                System.out.println("Controller返回类型：" + (result == null ? "null" : result.getClass().getName())); // 调试日志：确认返回Map

                // 3. 处理返回结果（转发/重定向/JSON）
                handleResult(result, req, resp);

            } catch (InvocationTargetException e) {
                // 捕获Controller方法内部抛出的异常
                Throwable targetException = e.getTargetException();
                // 【修改1：显式获取Writer并强制刷新，确保内容写入】
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/plain;charset=UTF-8");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                String errorMsg = "Controller方法执行异常：" + targetException.getMessage();
                // 强制写入并刷新
                PrintWriter writer = resp.getWriter();
                writer.write(errorMsg);
                writer.flush(); // 关键：避免内容留在缓冲区
                System.out.println("异常响应：" + errorMsg); // 调试日志
                return;

            } catch (IllegalAccessException e) {
                // 【修改2：同样处理权限异常】
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/plain;charset=UTF-8");
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                String errorMsg = "无法访问Controller方法（请确保方法为public）：" + e.getMessage();
                PrintWriter writer = resp.getWriter();
                writer.write(errorMsg);
                writer.flush();
                System.out.println("异常响应：" + errorMsg);
                return;

            } catch (IllegalArgumentException e) {
                // 【修改3：参数异常处理】
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/plain;charset=UTF-8");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String errorMsg = "方法参数不匹配：" + e.getMessage();
                PrintWriter writer = resp.getWriter();
                writer.write(errorMsg);
                writer.flush();
                System.out.println("异常响应：" + errorMsg);
                return;

            } catch (Exception e) {
                // 【修改4：其他异常处理】
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/plain;charset=UTF-8");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                String errorMsg = "处理请求[" + requestKey + "]失败：" + e.getMessage();
                PrintWriter writer = resp.getWriter();
                writer.write(errorMsg);
                writer.flush();
                System.out.println("异常响应：" + errorMsg);
                return;
            }
        } else {
            // 未找到映射：返回404
            resp.setStatus(404);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("Not Found: " + requestKey);
        }
    }

    /**
     * 解析方法参数：支持HttpServletRequest/HttpServletResponse/HttpSession，无参返回空数组
     */
    private Object[] resolveMethodParameters(Method method, HttpServletRequest req, HttpServletResponse resp) {
        Class<?>[] paramTypes = method.getParameterTypes();
        java.lang.annotation.Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            // 1. 支持Servlet原生对象
            if (paramType == HttpServletRequest.class) {
                args[i] = req;
                continue;
            }
            if (paramType == HttpServletResponse.class) {
                args[i] = resp;
                continue;
            }
            if (paramType == HttpSession.class) {
                args[i] = req.getSession();
                continue;
            }

            // 2. 检查是否有@RequestParam
            RequestParam requestParam = null;
            for (java.lang.annotation.Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof RequestParam) {
                    requestParam = (RequestParam) annotation;
                    break;
                }
            }

            if (requestParam != null) {
                // 获取参数名与默认值
                String paramName = requestParam.value();
                String defaultValue = requestParam.defaultValue();
                boolean required = requestParam.required();

                String paramValue = req.getParameter(paramName);
                if (paramValue == null || paramValue.isEmpty()) {
                    if (required && defaultValue.isEmpty()) {
                        throw new IllegalArgumentException("缺少必须的请求参数：" + paramName);
                    }
                    paramValue = defaultValue;
                }

                // 简单类型转换
                args[i] = convertType(paramValue, paramType);
            } else {
                args[i] = null;
                System.out.println("不支持的参数类型：" + paramType.getName() + "，暂传null");
            }
        }
        return args;
    }

    /** 简单类型转换（String→int/long/boolean等） */
    private Object convertType(String value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType == String.class) return value;
        if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(value);
        if (targetType == long.class || targetType == Long.class) return Long.parseLong(value);
        if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value);
        if (targetType == double.class || targetType == Double.class) return Double.parseDouble(value);
        return value; // 其他类型暂不支持
    }


    /**
     * 统一处理返回结果：根据结果类型分发到不同处理器
     *
     * 优先级顺序：
     * 1. ViewResult - 允许应用完全控制响应
     * 2. String - 兼容现有字符串返回方式
     * 3. Map - 返回 JSON
     * 4. 其他类型 - 返回文本
     */
    private void handleResult(Object result, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        resp.setCharacterEncoding("UTF-8"); // 全局设置字符编码，避免中文乱码

        if (result == null) {
            // 返回null：不处理（避免后续分支错误）
            System.out.println("result is null.");
            return;
        }

        // 1. 优先处理 ViewResult - 允许应用接管响应处理
        if (result instanceof ViewResult) {
            try {
                ((ViewResult) result).render(req, resp);
                System.out.println("ViewResult 响应：" + result.getClass().getSimpleName());
            } catch (Exception e) {
                throw new ServletException("渲染 ViewResult 失败: " + e.getMessage(), e);
            }
            return;
        }

        // 2. 字符串结果：转发或重定向（兼容现有方式）
        if (result instanceof String) {
            handleStringResult((String) result, req, resp);
            return;
        }

        // 3. Map结果：返回JSON
        if (result instanceof Map) {
            handleJsonResult((Map<?, ?>) result, resp);
            return;
        }

        // 4. 其他类型：默认返回文本
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().write(result.toString());
    }

    /**
     * 处理字符串结果：重定向（redirect:前缀）或JSP转发
     */
    private void handleStringResult(String viewName, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        if (viewName == null || viewName.trim().isEmpty()) {
            return;
        }
        viewName = viewName.trim();

        // 1. 处理重定向（redirect:前缀）
        if (viewName.startsWith("redirect:")) {
            String redirectUrl = viewName.substring("redirect:".length()).trim();
            resp.setStatus(302);
            resp.sendRedirect(redirectUrl);
            System.out.println("重定向到：" + redirectUrl);
            return;
        }

        // 2. 处理 HTML 响应（html:前缀）
        if (viewName.startsWith("html:")) {
            String htmlContent = viewName.substring("html:".length());
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write(htmlContent);
            writer.flush();
            resp.flushBuffer();
            System.out.println("HTML响应：" + (htmlContent.length() > 50 ? htmlContent.substring(0, 50) + "..." : htmlContent));
            return;
        }

        // 3. 处理内部转发（forward:前缀）
        if (viewName.startsWith("forward:")) {
            String forwardPath = viewName.substring("forward:".length()).trim();
            if (forwardPath.isEmpty()) {
                throw new ServletException("forward:指令后路径不能为空");
            }
            req.setAttribute("forwardPath", forwardPath);
            RequestDispatcher dispatcher = req.getRequestDispatcher(forwardPath);

            if (dispatcher != null) {
                resp.setStatus(200);
                dispatcher.forward(req, resp);
                System.out.println("内部转发到：" + forwardPath);
            } else {
                resp.setStatus(404);
                resp.getWriter().write("Forward path not found: " + forwardPath);
            }
            return;
        }

        // 3. 处理纯文本响应（不包含路径分隔符的简单字符串）
        if (!viewName.contains("/") && !viewName.contains("\\") && !viewName.endsWith(".jsp")) {
            resp.setContentType("text/plain;charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write(viewName);
            writer.flush();
            resp.flushBuffer();
            System.out.println("文本响应：" + viewName);
            return;
        }

        // 4. 处理JSP转发（原有逻辑不变）—— 仅对JSP路径生效
        String jspPath;
        if (viewName.contains("/WEB-INF/")) {
            jspPath = viewName;
        } else if (viewName.endsWith(".jsp")) {
            jspPath = "/WEB-INF/views/" + viewName;
        } else {
            jspPath = "/WEB-INF/views/" + viewName + ".jsp";
        }
        req.setAttribute("forwardPath", jspPath);
        RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);

        if (dispatcher != null) {
            resp.setStatus(200);
            dispatcher.forward(req, resp);
        } else {
            resp.setStatus(404);
            resp.getWriter().write("View not found: " + jspPath);
        }
    }

    /**
     * 处理Map结果：返回JSON格式（确保Content-Type正确设置）
     */
    private void handleJsonResult(Map<?, ?> dataMap, HttpServletResponse resp) throws IOException {
        // 1. 基础配置：设置Content-Type和字符编码（确保与测试预期一致）
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // 2. 处理空Map（避免生成"{}"以外的错误格式）
        if (dataMap == null || dataMap.isEmpty()) {
            resp.getWriter().write("{}");
            resp.flushBuffer(); // 强制提交，确保内容写入
            System.out.println("JSON响应：{}");
            return;
        }

        // 3. 正确序列化JSON：区分值类型（字符串/数字/布尔）
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : dataMap.entrySet()) {
            // 3.1 处理Key：转义字符串，加双引号
            String key = escapeJsonString(String.valueOf(entry.getKey()));
            json.append("\"").append(key).append("\":");

            // 3.2 处理Value：区分类型（字符串加引号，数字/布尔不加引号）
            Object value = entry.getValue();
            if (value == null) {
                json.append("null"); // null值不加引号
            } else if (value instanceof String) {
                // 字符串类型：转义后加双引号
                String strValue = escapeJsonString(String.valueOf(value));
                json.append("\"").append(strValue).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                // 数字/布尔类型：直接拼接（不加引号）
                json.append(value);
            } else {
                // 其他类型（如日期）：转为字符串后加引号
                String otherValue = escapeJsonString(String.valueOf(value));
                json.append("\"").append(otherValue).append("\"");
            }

            // 3.3 加逗号（最后一个会在后续移除）
            json.append(",");
        }

        // 4. 移除最后一个多余的逗号（避免JSON格式错误）
        if (json.length() > 1 && json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }
        json.append("}");

        // 5. 写入响应体并强制提交（确保Mock能获取到内容）
        String jsonStr = json.toString();
        resp.getWriter().write(jsonStr);
        resp.flushBuffer(); // 关键：强制提交响应，避免内容留在缓冲区

        // 调试日志：验证生成的JSON
        System.out.println("生成的JSON响应：" + jsonStr);
    }

    // 保留原有的字符串转义方法（确保特殊字符正确处理）
    private String escapeJsonString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 销毁Servlet：关闭Spring容器，释放资源
     */
    @Override
    public void destroy() {
        if (applicationContext != null) {
            applicationContext.close();
            System.out.println("SpringWindApplicationContext已关闭");
        }
    }
}