package com.github.microwind.springwind.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * JSON 响应结果
 */
public class JsonResult implements ViewResult {
    private final Object data;
    private String contentType = "application/json;charset=UTF-8";
    private String encoding = "UTF-8";

    public JsonResult(Object data) {
        this.data = data;
    }

    public JsonResult contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public JsonResult encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding(encoding);
        response.setContentType(contentType);

        String jsonStr;
        if (data == null) {
            jsonStr = "null";
        } else if (data instanceof String) {
            // 如果已经是 JSON 字符串，直接返回
            jsonStr = (String) data;
        } else if (data instanceof Map) {
            jsonStr = toJson((Map<?, ?>) data);
        } else {
            // 其他类型转为字符串并加引号
            jsonStr = "\"" + escapeJsonString(String.valueOf(data)) + "\"";
        }

        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.flush();
        response.flushBuffer();
    }

    /**
     * 简单的 Map 转 JSON 实现
     */
    private String toJson(Map<?, ?> dataMap) {
        if (dataMap == null || dataMap.isEmpty()) {
            return "{}";
        }

        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<?, ?> entry : dataMap.entrySet()) {
            String key = escapeJsonString(String.valueOf(entry.getKey()));
            json.append("\"").append(key).append("\":");

            Object value = entry.getValue();
            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                String strValue = escapeJsonString(String.valueOf(value));
                json.append("\"").append(strValue).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else {
                String otherValue = escapeJsonString(String.valueOf(value));
                json.append("\"").append(otherValue).append("\"");
            }

            json.append(",");
        }

        if (json.length() > 1 && json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }
        json.append("}");

        return json.toString();
    }

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
}
