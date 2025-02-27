package com.javaweborder.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class ResponseUtils {

    // 发送 JSON 格式的标准响应
    public static void sendJsonResponse(HttpServletResponse response, int statusCode, Object data, Map<String, String> headers) throws IOException {
        sendResponse(response, statusCode, data, "application/json", headers);
    }

    // 发送 JSON 格式的错误响应
    public static void sendJsonError(HttpServletResponse response, int statusCode, String message, Map<String, String> headers) throws IOException {
        sendError(response, statusCode, message, "application/json", headers);
    }

    // 发送标准响应
    private static void sendResponse(HttpServletResponse response, int statusCode, Object data, String contentType, Map<String, String> headers) throws IOException {
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/json";
        }

        // 设置响应头
        response.setStatus(statusCode);
        response.setContentType(contentType);

        // 设置自定义头部
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                response.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 根据不同的 contentType 处理响应内容
        try (OutputStream out = response.getOutputStream()) {
            if ("application/json".equals(contentType)) {
                out.write(data.toString().getBytes());
            } else if ("text/plain".equals(contentType)) {
                out.write(data.toString().getBytes());
            } else if ("application/xml".equals(contentType)) {
                out.write(data.toString().getBytes()); // 可以在这里加入XML格式化逻辑
            } else if ("text/html".equals(contentType)) {
                out.write(data.toString().getBytes()); // 可以返回HTML内容
            } else if ("application/octet-stream".equals(contentType)) {
                out.write((byte[]) data); // 用于二进制数据
            } else {
                out.write(data.toString().getBytes());
            }
        }
    }

    // 发送错误响应
    private static void sendError(HttpServletResponse response, int statusCode, String message, String contentType, Map<String, String> headers) throws IOException {
        if (contentType == null) {
            contentType = "application/json";
        }

        // 设置响应头
        response.setStatus(statusCode);
        response.setContentType(contentType);

        // 设置自定义头部
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                response.setHeader(entry.getKey(), entry.getValue());
            }
        }

        // 错误响应体
        String errorResponse = "application/json".equals(contentType) ? "{\"error\":\"" + message + "\"}" : message;

        try (OutputStream out = response.getOutputStream()) {
            if ("application/json".equals(contentType)) {
                out.write(errorResponse.getBytes());
            } else {
                out.write(errorResponse.getBytes());
            }
        }
    }

    // 发送空响应（204 No Content）
    public static void sendNoContent(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.setContentLength(0);
        response.getOutputStream().flush();
    }

    // 发送文件
    public static void sendFile(HttpServletResponse response, String filePath, String fileName, String contentType) throws IOException {
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        File file = new File(filePath);
        if (!file.exists()) {
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "无法读取文件", null);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Content-Length", String.valueOf(file.length()));

        try (FileInputStream fileInputStream = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        }
    }

    // 设置缓存头
    public static void setCacheHeaders(HttpServletResponse response, int cacheDuration) {
        response.setHeader("Cache-Control", "public, max-age=" + cacheDuration);
        long expirationDate = System.currentTimeMillis() + (cacheDuration * 1000L);
        response.setHeader("Expires", new java.util.Date(expirationDate).toString());
    }

    // 设置 CORS 头
    public static void setCorsHeaders(HttpServletResponse response, String origin) {
        if (origin == null || origin.isEmpty()) {
            origin = "*";
        }

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
