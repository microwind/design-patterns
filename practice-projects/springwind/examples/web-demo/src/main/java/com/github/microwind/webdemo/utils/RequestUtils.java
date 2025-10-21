package com.github.microwind.webdemo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class RequestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 从请求体中读取JSON并转换为指定类型
     */
    public static <T> T readJsonBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String json = sb.toString();
        System.out.println("[DEBUG] 请求JSON: " + json);
        return objectMapper.readValue(json, clazz);
    }

    /**
     * 从URL路径中提取ID参数
     * 例如: /article/detail/123 -> 123
     */
    public static Long extractIdFromPath(String requestURI, String basePath) {
        String idStr = requestURI.substring(basePath.length());
        if (idStr.startsWith("/")) {
            idStr = idStr.substring(1);
        }
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
