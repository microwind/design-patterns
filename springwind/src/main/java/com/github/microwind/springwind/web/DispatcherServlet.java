package com.github.microwind.springwind.web;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.core.SpringWindApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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
            // 1. 获取配置类的全限定名（从ServletConfig中获取，如"com.github.microwind.springwind.MvcTest"）
            String configClassName = getInitParameter("configClass");
            if (configClassName == null || configClassName.trim().isEmpty()) {
                throw new ServletException("未配置configClass参数（需指定Spring配置类的全限定名）");
            }

            // 2. 将字符串类名转换为Class对象
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
                basePath = normalizePath(clazz.getAnnotation(RequestMapping.class).value());
            }

            // 2. 解析方法级别@RequestMapping（使用getMethods()获取包括继承的公共方法）
            for (Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);

                    // 处理方法路径格式
                    String methodPath = normalizePath(requestMapping.value());

                    // 拼接完整路径（类路径 + 方法路径）
                    String fullPath = combinePaths(basePath, methodPath);

                    // 获取HTTP方法（如"GET"、"POST"），提供默认值
                    String httpMethod = requestMapping.method().trim();
                    if (httpMethod.isEmpty()) {
                        httpMethod = "GET"; // 默认GET方法
                    }
                    httpMethod = httpMethod.toUpperCase(); // 统一大写

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

    /**
     * 规范化路径，确保格式正确
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
        // 去除尾部斜杠（除非是根路径）
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * 合并两个路径，处理中间可能出现的双斜杠问题
     */
    private String combinePaths(String path1, String path2) {
        if (path1.isEmpty()) {
            return path2;
        }
        if (path2.isEmpty()) {
            return path1;
        }

        // 确保中间只有一个斜杠
        if (path1.endsWith("/") && path2.startsWith("/")) {
            return path1 + path2.substring(1);
        } else if (!path1.endsWith("/") && !path2.startsWith("/")) {
            return path1 + "/" + path2;
        } else {
            return path1 + path2;
        }
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
                // 解析方法参数
                Object[] methodArgs = resolveMethodParameters(handlerMapping.getMethod(), req, resp);

                // 调用处理方法
                Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), methodArgs);

                // 处理返回结果
                handleResult(result, req, resp);
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof ServletException) {
                    throw (ServletException) targetException;
                } else if (targetException instanceof IOException) {
                    throw (IOException) targetException;
                } else {
                    throw new ServletException("Controller方法执行异常", targetException);
                }
            } catch (IllegalAccessException e) {
                throw new ServletException("无法访问Controller方法", e);
            } catch (IllegalArgumentException e) {
                throw new ServletException("方法参数不匹配", e);
            } catch (Exception e) {
                throw new ServletException("处理请求[" + key + "]失败", e);
            }
        } else {
            resp.setStatus(404);
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().write("Not Found: " + key); // 输出未匹配的Key，便于调试
        }
    }

    /**
     * 解析方法参数，支持多种参数类型
     */
    private Object[] resolveMethodParameters(Method method, HttpServletRequest req, HttpServletResponse resp) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i] == HttpServletRequest.class) {
                args[i] = req;
            } else if (paramTypes[i] == HttpServletResponse.class) {
                args[i] = resp;
            } else if (paramTypes[i] == HttpSession.class) {
                args[i] = req.getSession();
            } else {
                // 可以扩展支持更多类型，如 @RequestParam 注解参数等
                // 目前对于不支持的类型传入null，后续可以扩展
                args[i] = null;
            }
        }
        return args;
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

        if (result == null) {
            // 如果返回null，不进行任何处理
            return;
        } else if (result instanceof String) {
            handleStringResult((String) result, req, resp);
        } else if (result instanceof Map) {
            handleJsonResult((Map<?, ?>) result, resp);
        } else {
            // 其他类型，默认按字符串处理
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().write(result.toString());
        }
    }

    /**
     * 处理字符串类型的结果（视图跳转）
     */
    private void handleStringResult(String viewName, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        if (viewName.startsWith("redirect:")) {
            // 重定向：去掉"redirect:"前缀
            String redirectUrl = viewName.substring("redirect:".length());
            resp.sendRedirect(redirectUrl);
        } else {
            // 转发到JSP：路径格式为/WEB-INF/views/xxx.jsp
            String jspPath = "/WEB-INF/views/" + viewName + ".jsp";
            req.getRequestDispatcher(jspPath).forward(req, resp);
        }
    }

    /**
     * 处理JSON类型的结果
     */
    private void handleJsonResult(Map<?, ?> dataMap, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : dataMap.entrySet()) {
            String key = escapeJsonString(String.valueOf(entry.getKey()));
            String value = escapeJsonString(String.valueOf(entry.getValue()));
            json.append("\"").append(key).append("\":\"").append(value).append("\",");
        }
        if (json.length() > 1) {
            json.deleteCharAt(json.length() - 1); // 去掉最后一个逗号
        }
        json.append("}");
        resp.getWriter().write(json.toString());
    }

    /**
     * 转义JSON字符串中的特殊字符
     */
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
     * 销毁Servlet，关闭Spring容器
     */
    @Override
    public void destroy() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }
}