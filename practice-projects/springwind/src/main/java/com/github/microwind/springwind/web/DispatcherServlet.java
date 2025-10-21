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
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SpringWind DispatcherServlet - 前端控制器
 * 负责处理所有HTTP请求，将请求分发给对应的Controller处理
 *
 * 兼容特性：
 * - 支持类级与方法级 @RequestMapping
 * - 支持方法参数注入：HttpServletRequest/HttpServletResponse/HttpSession、@RequestParam 简单类型
 * - 支持返回值类型：ViewResult / String / Map -> JSON / 其他 -> 文本
 * - 支持 redirect:, forward:, html: 等字符串约定
 * - 对路径做归一化处理，避免尾斜杠问题
 */
public class DispatcherServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(DispatcherServlet.class.getName());

    /**
     * 映射表：Key = "HTTP_METHOD:/normalized/path"，Value = HandlerMapping
     * 例如: "GET:/home/index" -> HandlerMapping(controllerInstance, method)
     */
    private final Map<String, HandlerMapping> handlerMappings = new HashMap<>();

    private SpringWindApplicationContext applicationContext;

    @Override
    public void init() throws ServletException {
        try {
            // 1. 获取配置类名（WebDemoApplication 传入的 configClass）
            String configClassName = getInitParameter("configClass");
            if (configClassName == null || configClassName.trim().isEmpty()) {
                throw new ServletException("未配置configClass参数（需指定Spring配置类的全限定名）");
            }

            log.info("[DispatcherServlet] init with configClass=" + configClassName);

            // 2. 将字符串类名转换为Class对象并初始化 SpringWindApplicationContext
            Class<?> configClass = Class.forName(configClassName);
            applicationContext = new SpringWindApplicationContext(configClass);

            // 3. 初始化 handler 映射
            initHandlerMappings();

            log.info("[DispatcherServlet] 初始化完成，已注册映射数量: " + handlerMappings.size());
        } catch (ClassNotFoundException e) {
            throw new ServletException("找不到配置类：" + getInitParameter("configClass"), e);
        } catch (Exception e) {
            throw new ServletException("DispatcherServlet 初始化失败", e);
        }
    }

    /**
     * 扫描所有 @Controller Bean 并根据 @RequestMapping 构建映射表
     */
    private void initHandlerMappings() {
        // 从 SpringWindApplicationContext 获取被 @Controller 注解的 bean（name -> instance）
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);

        if (controllers == null || controllers.isEmpty()) {
            // 选择抛出异常以便尽早发现问题（如果希望容错可改为返回）
            throw new RuntimeException("未扫描到任何 @Controller 注解的 Bean，请检查包扫描范围和配置类");
        }

        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            Object controller = entry.getValue();
            Class<?> clazz = controller.getClass();

            // 解析类级别 @RequestMapping（作为 base path）
            String basePath = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping classMapping = clazz.getAnnotation(RequestMapping.class);
                basePath = classMapping.value();
            }
            basePath = normalizePath(basePath);

            // 遍历 public 方法（包含继承的方法）
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                String methodPath = normalizePath(methodMapping.value());
                String fullPath = combinePaths(basePath, methodPath);

                // 解析HTTP方法（若未指定，默认GET）
                String httpMethod = methodMapping.method() == null ? "" : methodMapping.method().trim();
                if (httpMethod.isEmpty()) httpMethod = "GET";
                httpMethod = httpMethod.toUpperCase(Locale.ROOT);

                String mappingKey = httpMethod + ":" + normalizePath(fullPath);

                // 检查重复映射
                if (handlerMappings.containsKey(mappingKey)) {
                    throw new RuntimeException("存在重复的请求映射：" + mappingKey);
                }

                handlerMappings.put(mappingKey, new HandlerMapping(controller, method));
                log.info("[DispatcherServlet] 生成请求映射：" + mappingKey + " -> " +
                        clazz.getName() + "#" + method.getName());
            }
        }
    }

    /**
     * service 方法：匹配 Handler -> 解析参数 -> 调用 Controller -> 处理返回结果
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 获取请求路径（兼容 servletPath/pathInfo/requestURI），并进行归一化
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
        path = normalizePath(path);

        String httpMethod = req.getMethod().toUpperCase(Locale.ROOT);
        String requestKey = httpMethod + ":" + path;

        log.fine("[DispatcherServlet] 当前请求Key：" + requestKey);

        HandlerMapping handlerMapping = handlerMappings.get(requestKey);
        if (handlerMapping != null) {
            try {
                // 1. 解析方法参数（支持 HttpServletRequest/HttpServletResponse/HttpSession 和 @RequestParam）
                Object[] methodArgs = resolveMethodParameters(handlerMapping.getMethod(), req, resp);
                log.fine("[DispatcherServlet] 解析到的参数数量：" + methodArgs.length);

                // 2. 反射调用 Controller 方法
                Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), methodArgs);
                log.fine("[DispatcherServlet] Controller 返回类型：" +
                        (result == null ? "null" : result.getClass().getName()));

                // 3. 处理返回结果
                handleResult(result, req, resp);

            } catch (InvocationTargetException e) {
                // Controller 方法内部抛出的异常
                Throwable target = e.getTargetException();
                log.log(Level.SEVERE, "Controller 方法执行异常", target);
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Controller方法执行异常：" + (target == null ? e.getMessage() : target.getMessage()));
                return;
            } catch (IllegalAccessException e) {
                log.log(Level.WARNING, "无法访问Controller方法", e);
                writeError(resp, HttpServletResponse.SC_FORBIDDEN,
                        "无法访问Controller方法（请确保方法为public）：" + e.getMessage());
                return;
            } catch (IllegalArgumentException e) {
                log.log(Level.WARNING, "方法参数不匹配", e);
                writeError(resp, HttpServletResponse.SC_BAD_REQUEST,
                        "方法参数不匹配：" + e.getMessage());
                return;
            } catch (Exception e) {
                log.log(Level.SEVERE, "处理请求失败", e);
                writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "处理请求[" + requestKey + "]失败：" + e.getMessage());
                return;
            }
        } else {
            // 未找到映射：返回 404 明确提示
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("Not Found: " + requestKey);
            writer.flush();
        }
    }

    /**
     * 解析方法参数：支持 HttpServletRequest, HttpServletResponse, HttpSession, @RequestParam
     * - 无参方法返回空数组
     */
    private Object[] resolveMethodParameters(Method method, HttpServletRequest req, HttpServletResponse resp) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[i];

            // 支持 Servlet 原生对象
            if (HttpServletRequest.class.isAssignableFrom(paramType)) {
                args[i] = req;
                continue;
            }
            if (HttpServletResponse.class.isAssignableFrom(paramType)) {
                args[i] = resp;
                continue;
            }
            if (HttpSession.class.isAssignableFrom(paramType)) {
                args[i] = req.getSession();
                continue;
            }

            // 检查 @RequestParam 注解
            RequestParam rp = null;
            for (Annotation a : paramAnnotations[i]) {
                if (a instanceof RequestParam) {
                    rp = (RequestParam) a;
                    break;
                }
            }

            if (rp != null) {
                String paramName = rp.value();
                String defaultValue = rp.defaultValue();
                boolean required = rp.required();

                String paramValue = req.getParameter(paramName);
                if (paramValue == null || paramValue.isEmpty()) {
                    if (required && (defaultValue == null || defaultValue.isEmpty())) {
                        throw new IllegalArgumentException("缺少必须的请求参数：" + paramName);
                    }
                    paramValue = defaultValue;
                }

                args[i] = convertType(paramValue, paramType);
            } else {
                // 不支持的复杂类型，传 null（或可扩展支持 Body -> 对象绑定）
                args[i] = null;
                log.fine("[DispatcherServlet] 不支持的参数类型：" + paramType.getName() + "，暂传null");
            }
        }
        return args;
    }

    /** 简单类型转换 */
    private Object convertType(String value, Class<?> targetType) {
        if (value == null) return null;
        try {
            if (targetType == String.class) return value;
            if (targetType == int.class || targetType == Integer.class) return Integer.parseInt(value);
            if (targetType == long.class || targetType == Long.class) return Long.parseLong(value);
            if (targetType == boolean.class || targetType == Boolean.class) return Boolean.parseBoolean(value);
            if (targetType == double.class || targetType == Double.class) return Double.parseDouble(value);
        } catch (Exception e) {
            log.log(Level.WARNING, "参数转换失败: value=" + value + ", target=" + targetType.getName(), e);
        }
        return null;
    }

    /**
     * 统一处理返回结果：根据结果类型分发到不同处理器
     *
     * 优先级顺序：
     * 1. ViewResult - 允许应用完全控制响应
     * 2. String - 兼容现有字符串返回方式（包括 redirect:/ forward:/ html:）
     * 3. Map - 返回 JSON
     * 4. 其他类型 - 返回文本
     */
    private void handleResult(Object result, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        resp.setCharacterEncoding("UTF-8");

        if (result == null) {
            // 返回null：检查响应是否已提交
            // 如果已提交，说明Controller方法内部已手动处理响应（如通过ResponseUtils），不应覆盖
            // 如果未提交，说明真的没有内容，返回204
            if (resp.isCommitted()) {
                log.fine("[DispatcherServlet] result is null, response already committed by controller");
                return;
            }
            // 响应未提交，返回204表示没有内容
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            log.fine("[DispatcherServlet] result is null, return 204 No Content");
            return;
        }

        // 1. ViewResult（应用完全控制响应）
        if (result instanceof ViewResult) {
            try {
                ((ViewResult) result).render(req, resp);
                log.fine("[DispatcherServlet] ViewResult 响应：" + result.getClass().getSimpleName());
            } catch (Exception e) {
                throw new ServletException("渲染 ViewResult 失败: " + e.getMessage(), e);
            }
            return;
        }

        // 2. String 逻辑处理
        if (result instanceof String) {
            handleStringResult((String) result, req, resp);
            return;
        }

        // 3. Map -> JSON
        if (result instanceof Map) {
            handleJsonResult((Map<?, ?>) result, resp);
            return;
        }

        // 4. 其他类型 -> 文本
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write(result.toString());
        writer.flush();
    }

    /**
     * 处理字符串结果：redirect: / forward: / html: / 纯文本 / jsp
     */
    private void handleStringResult(String viewName, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        if (viewName == null || viewName.trim().isEmpty()) return;
        viewName = viewName.trim();

        // redirect:
        if (viewName.startsWith("redirect:")) {
            String redirectUrl = viewName.substring("redirect:".length()).trim();
            resp.setStatus(HttpServletResponse.SC_FOUND);
            resp.sendRedirect(redirectUrl);
            log.fine("[DispatcherServlet] 重定向到：" + redirectUrl);
            return;
        }

        // html:
        if (viewName.startsWith("html:")) {
            String html = viewName.substring("html:".length());
            resp.setContentType("text/html;charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write(html);
            writer.flush();
            resp.flushBuffer();
            log.fine("[DispatcherServlet] HTML 响应，长度: " + (html == null ? 0 : html.length()));
            return;
        }

        // forward:
        if (viewName.startsWith("forward:")) {
            String forwardPath = viewName.substring("forward:".length()).trim();
            if (forwardPath.isEmpty()) {
                throw new ServletException("forward: 指令后路径不能为空");
            }
            req.setAttribute("forwardPath", forwardPath);
            RequestDispatcher dispatcher = req.getRequestDispatcher(forwardPath);
            if (dispatcher != null) {
                resp.setStatus(HttpServletResponse.SC_OK);
                dispatcher.forward(req, resp);
                log.fine("[DispatcherServlet] 内部转发到：" + forwardPath);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("Forward path not found: " + forwardPath);
            }
            return;
        }

        // 区分"视图名称"和"纯文本响应"
        // 视图名称特征：纯字母/下划线/连字符组成（可能有点号），如 "userInfo", "admin.userList"
        // 纯文本特征：包含特殊字符（冒号/空格/数字开头等），如 "user:123:张三", "success", "123"
        boolean looksLikeViewName = !viewName.contains("/") &&
                                    !viewName.contains("\\") &&
                                    !viewName.endsWith(".jsp") &&
                                    !viewName.contains(":") &&  // 排除 "user:123:张三"
                                    !viewName.contains(" ") &&  // 排除包含空格的文本
                                    viewName.matches("[a-zA-Z][a-zA-Z0-9_.]*");  // 必须以字母开头，只包含字母数字下划线点

        if (looksLikeViewName) {
            // 视图名称 → JSP路径解析
            String jspPath = "/WEB-INF/views/" + viewName + ".jsp";
            req.setAttribute("forwardPath", jspPath);
            RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);
            resp.setStatus(HttpServletResponse.SC_OK);
            if (dispatcher != null) {
                dispatcher.forward(req, resp);
                log.fine("[DispatcherServlet] JSP 转发到（视图名称解析）：" + jspPath);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("View not found: " + jspPath);
                log.warning("[DispatcherServlet] 无法获取 RequestDispatcher: " + jspPath);
            }
            return;
        }

        // 纯文本响应（不符合视图名称特征的简单字符串）
        if (!viewName.contains("/") && !viewName.contains("\\") && !viewName.endsWith(".jsp")) {
            resp.setContentType("text/plain;charset=UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write(viewName);
            writer.flush();
            resp.flushBuffer();
            log.fine("[DispatcherServlet] 文本响应：" + viewName);
            return;
        }

        // JSP 转发（包含路径或.jsp后缀的字符串）
        String jspPath;
        if (viewName.contains("/WEB-INF/")) {
            jspPath = viewName;
        } else if (viewName.endsWith(".jsp")) {
            jspPath = viewName;
        } else {
            jspPath = "/WEB-INF/views/" + viewName + ".jsp";
        }
        req.setAttribute("forwardPath", jspPath);
        RequestDispatcher dispatcher = req.getRequestDispatcher(jspPath);
        // 在测试环境或开发环境中，dispatcher 永远不为 null（由 Mock 或容器提供）
        // 直接调用 forward，让容器或 Mock 处理
        resp.setStatus(HttpServletResponse.SC_OK);
        if (dispatcher != null) {
            dispatcher.forward(req, resp);
            log.fine("[DispatcherServlet] JSP 转发到：" + jspPath);
        } else {
            // 容器无法提供 dispatcher（非常罕见），返回错误
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("View not found: " + jspPath);
            log.warning("[DispatcherServlet] 无法获取 RequestDispatcher: " + jspPath);
        }
    }

    /**
     * 处理 JSON 返回（Map -> JSON）
     */
    private void handleJsonResult(Map<?, ?> dataMap, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (dataMap == null || dataMap.isEmpty()) {
            resp.getWriter().write("{}");
            resp.flushBuffer();
            log.fine("[DispatcherServlet] JSON 响应：{}");
            return;
        }

        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : dataMap.entrySet()) {
            String key = escapeJsonString(String.valueOf(entry.getKey()));
            json.append("\"").append(key).append("\":");
            Object value = entry.getValue();
            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                json.append("\"").append(escapeJsonString(String.valueOf(value))).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(escapeJsonString(String.valueOf(value))).append("\"");
            }
            json.append(",");
        }

        if (json.length() > 1 && json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }
        json.append("}");

        String jsonStr = json.toString();
        resp.getWriter().write(jsonStr);
        resp.flushBuffer();
        log.fine("[DispatcherServlet] 生成的JSON响应：" + jsonStr);
    }

    private String escapeJsonString(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 统一错误返回（JSON格式）
     */
    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String body = "{\"error\":\"" + escapeJsonString(message) + "\"}";
        resp.getWriter().write(body);
        resp.getWriter().flush();
        resp.flushBuffer();
        log.warning("[DispatcherServlet] 错误响应: status=" + status + " message=" + message);
    }

    /** 规范化路径：去重重复斜杠，确保以 / 开头，去尾部 /（根路径除外） */
    private String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) return "/";
        path = path.trim().replaceAll("/+", "/");
        if (!path.startsWith("/")) path = "/" + path;
        if (path.length() > 1 && path.endsWith("/")) path = path.substring(0, path.length() - 1);
        return path;
    }

    /** 合并 base 和 sub 路径并归一化 */
    private String combinePaths(String basePath, String methodPath) {
        if (basePath == null) basePath = "";
        if (methodPath == null) methodPath = "";
        basePath = basePath.trim();
        methodPath = methodPath.trim();

        if (basePath.isEmpty()) {
            return normalizePath(methodPath);
        }
        if (methodPath.isEmpty()) {
            return normalizePath(basePath);
        }
        // 保证只有单个斜杠连接
        if (basePath.endsWith("/") && methodPath.startsWith("/")) {
            return normalizePath(basePath + methodPath.substring(1));
        } else if (!basePath.endsWith("/") && !methodPath.startsWith("/")) {
            return normalizePath(basePath + "/" + methodPath);
        } else {
            return normalizePath(basePath + methodPath);
        }
    }

    /**
     * HandlerMapping 封装
     */
    private static class HandlerMapping {
        private final Object controller;
        private final Method method;

        public HandlerMapping(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }

        public Object getController() {
            return controller;
        }

        public Method getMethod() {
            return method;
        }
    }

    @Override
    public void destroy() {
        if (applicationContext != null) {
            try {
                applicationContext.close();
                log.info("[DispatcherServlet] SpringWindApplicationContext 已关闭");
            } catch (Exception e) {
                log.log(Level.WARNING, "关闭 applicationContext 时异常", e);
            }
        }
    }
}
