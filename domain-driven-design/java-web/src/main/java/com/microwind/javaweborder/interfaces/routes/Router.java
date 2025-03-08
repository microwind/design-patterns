// 基于Servlet规范的路由工具函数
package com.microwind.javaweborder.interfaces.routes;

import com.microwind.javaweborder.utils.LogUtils;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Route 定义路由结构
class Route {
    public String method;
    public Pattern pattern;
    public RouteHandler handler;
    public List<String> paramNames;

    public Route(String method, String pathPattern, RouteHandler handler) {
        this.method = method.toUpperCase();
        this.paramNames = extractParamNames(pathPattern);
        this.pattern = compilePattern(pathPattern);
        this.handler = handler;
    }

    // 编译路径匹配的正则表达式（对字面量部分转义，并替换路径参数为命名捕获组）
    private Pattern compilePattern(String pathPattern) {
        StringBuilder regex = new StringBuilder("^");
        // Matcher m = Pattern.compile("\\{(\\w+)}").matcher(pathPattern);
        // 修改正则匹配 {id} 为 :id
        Matcher m = Pattern.compile(":(\\w+)").matcher(pathPattern);
        int lastIndex = 0;
        while (m.find()) {
            regex.append(Pattern.quote(pathPattern.substring(lastIndex, m.start())));
            regex.append("(?<").append(m.group(1)).append(">[^/]+)");
            lastIndex = m.end();
        }
        regex.append(Pattern.quote(pathPattern.substring(lastIndex)));
        regex.append("$");
        return Pattern.compile(regex.toString());
    }

    // 提取路径中的参数名称
    private List<String> extractParamNames(String pathPattern) {
        List<String> paramNames = new ArrayList<>();
        // Matcher matcher = Pattern.compile("\\{(\\w+)}").matcher(pathPattern);
        // 修改正则匹配 {id} 为 :id
        Matcher matcher = Pattern.compile(":(\\w+)").matcher(pathPattern);
        while (matcher.find()) {
            paramNames.add(matcher.group(1));
        }
        return paramNames;
    }
}

// Router 定义路由管理器
public class Router extends HttpServlet {
    private final List<Route> routes = new CopyOnWriteArrayList<>();

    // 注册 GET 请求的路由
    public void get(String pathPattern, RouteHandler handler) {
        routes.add(new Route("GET", pathPattern, handler));
    }

    // 注册 POST 请求的路由
    public void post(String pathPattern, RouteHandler handler) {
        routes.add(new Route("POST", pathPattern, handler));
    }

    // 注册 PUT 请求的路由
    public void put(String pathPattern, RouteHandler handler) {
        routes.add(new Route("PUT", pathPattern, handler));
    }

    // 注册 DELETE 请求的路由
    public void delete(String pathPattern, RouteHandler handler) {
        routes.add(new Route("DELETE", pathPattern, handler));
    }

    // 处理所有请求
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置请求和响应编码为 UTF-8
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getRequestURI().substring(req.getContextPath().length());
        String method = req.getMethod().toUpperCase();

        /* 关闭打印，调试再打开
        // LogUtils.logInfo("Request URI: " + req.getRequestURI());
        // LogUtils.logInfo("Context Path: " + req.getContextPath());
        // LogUtils.logInfo("Processed Path: " + path);
        // LogUtils.logInfo("out Received request: " + method + " " + path);
         */

        for (Route route : routes) {
            Matcher matcher = route.pattern.matcher(path);
            if (route.method.equals(method) && matcher.matches()) {
                // 提取路径参数并设置到 request 属性中
                Map<String, String> pathParams = extractPathParams(matcher, route.paramNames);
                pathParams.forEach(req::setAttribute);
                try {
                    route.handler.handle(req, resp);
                } catch (Exception e) {
                    handleException(e, resp);
                }
                return;
            }
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
    }

    private Map<String, String> extractPathParams(Matcher matcher, List<String> paramNames) {
        Map<String, String> params = new HashMap<>();
        for (String paramName : paramNames) {
            params.put(paramName, matcher.group(paramName));
        }
        return params;
    }

    private void handleException(Exception e, HttpServletResponse resp) {
        try {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            LogUtils.logError("handleException:", e);
        } catch (IOException ioException) {
            LogUtils.logError("handleException:", ioException);
        }
    }
}