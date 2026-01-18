package com.github.microwind.springwind.web;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * HTTP 请求工具类
 * 提供请求体读取等常用功能
 */
public final class HttpRequestUtil {

    private HttpRequestUtil() {
    }

    /**
     * 读取请求体内容
     * @param request HTTP 请求对象
     * @return 请求体字符串
     * @throws IOException 读取失败时抛出
     */
    public static String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * 读取请求体内容（使用字节缓冲区方式）
     * @param request HTTP 请求对象
     * @return 请求体字符串
     * @throws IOException 读取失败时抛出
     */
    public static String getRequestBodyByBytes(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = request.getInputStream().read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    /**
     * 获取客户端 IP 地址
     * 支持通过代理获取真实 IP
     * @param request HTTP 请求对象
     * @return 客户端 IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // X-Forwarded-For 可能包含多个 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 判断是否为 AJAX 请求
     * @param request HTTP 请求对象
     * @return 是否为 AJAX 请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equalsIgnoreCase(header);
    }

    /**
     * 判断是否为 JSON 请求
     * @param request HTTP 请求对象
     * @return 是否为 JSON 请求
     */
    public static boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }
}
