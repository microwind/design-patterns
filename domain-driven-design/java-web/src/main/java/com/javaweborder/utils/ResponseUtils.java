package com.javaweborder.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseUtils {
    // 全局ObjectMapper配置（线程安全）
    private static final ObjectMapper objectMapper;
    // 使用静态代码块来初始化 ObjectMapper 实例，避免了双花括号初始化带来的问题。
    // 静态代码块在类加载时执行一次，保证了 ObjectMapper 实例的全局唯一性和线程安全性
    static {
        objectMapper = new ObjectMapper();
        // 如果使用较新的 Jackson 版本，不需要这个配置
        // objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
    }


    // 发送JSON响应（支持中文）
    public static void sendJsonResponse(HttpServletResponse response, int statusCode,
                                        Object data, Map<String, String> headers) throws IOException {
        sendResponse(response, statusCode, data, "application/json;charset=UTF-8", headers);
    }

    // 发送JSON错误响应（支持中文）
    public static void sendJsonError(HttpServletResponse response, int statusCode,
                                     String message, Map<String, String> headers) throws IOException {
        sendError(response, statusCode, message, "application/json;charset=UTF-8", headers);
    }

    // 核心响应方法
    private static void sendResponse(HttpServletResponse response, int statusCode,
                                     Object data, String contentType, Map<String, String> headers) throws IOException {
        // 设置基础响应头
        response.setStatus(statusCode);
        response.setContentType(contentType != null ? contentType : "application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 设置自定义头
        if (headers != null) {
            headers.forEach(response::setHeader);
        }

        // 生成JSON字符串
        String json = objectMapper.writeValueAsString(data);
        System.out.println("[DEBUG] 响应JSON: " + json);  // 打印中文日志

        // 写入响应体
        try (OutputStream out = response.getOutputStream()) {
            out.write(json.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }

    // 错误响应核心方法
    private static void sendError(HttpServletResponse response, int statusCode,
                                  String message, String contentType, Map<String, String> headers) throws IOException {
        // 设置基础响应头
        response.setStatus(statusCode);
        response.setContentType(contentType != null ? contentType : "application/json;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // 设置自定义头
        if (headers != null) {
            headers.forEach(response::setHeader);
        }

        // 构建错误响应体
        ErrorResponse error = new ErrorResponse(message);
        String json = objectMapper.writeValueAsString(error);
        System.out.println("[ERROR] 错误响应: " + json);

        // 写入响应体
        try (OutputStream out = response.getOutputStream()) {
            out.write(json.getBytes(StandardCharsets.UTF_8));
            out.flush();
        }
    }

    // 空响应（204）
    public static void sendNoContent(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.setContentLength(0);
        response.getOutputStream().flush();
    }

    // 文件下载（保留原功能）
    public static void sendFile(HttpServletResponse response, String filePath,
                                String fileName, String contentType) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "文件不存在: " + filePath, null);
            return;
        }

        response.reset();
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentLengthLong(file.length());

        try (FileInputStream fis = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    // 缓存控制（保留原功能）
    public static void setCacheHeaders(HttpServletResponse response, int cacheDuration) {
        response.setHeader("Cache-Control", "public, max-age=" + cacheDuration);
        response.setDateHeader("Expires", System.currentTimeMillis() + cacheDuration * 1000L);
    }

    // CORS配置（增强版）
    public static void setCorsHeaders(HttpServletResponse response, String allowedOrigin) {
        response.setHeader("Access-Control-Allow-Origin", allowedOrigin != null ? allowedOrigin : "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    // 错误响应内部类
    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}