package com.github.microwind.springwind.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

/**
 * JSON 响应结果
 * 支持对象、Map、List 的 JSON 序列化
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

        String jsonStr = toJson(data);

        PrintWriter writer = response.getWriter();
        writer.write(jsonStr);
        writer.flush();
        response.flushBuffer();
    }

    /**
     * 将对象转换为 JSON 字符串（支持嵌套对象）
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof String) {
            return "\"" + escapeJsonString((String) obj) + "\"";
        }

        if (obj instanceof Number || obj instanceof Boolean) {
            return String.valueOf(obj);
        }

        if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj);
        }

        if (obj instanceof Collection) {
            return collectionToJson((Collection<?>) obj);
        }

        if (obj.getClass().isArray()) {
            return arrayToJson(obj);
        }

        // 其他对象转为字符串
        return "\"" + escapeJsonString(String.valueOf(obj)) + "\"";
    }

    /**
     * Map 转 JSON
     */
    private String mapToJson(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }

        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            String key = escapeJsonString(String.valueOf(entry.getKey()));
            json.append("\"").append(key).append("\":");
            json.append(toJson(entry.getValue()));
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Collection 转 JSON
     */
    private String collectionToJson(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        for (Object item : collection) {
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append(toJson(item));
        }

        json.append("]");
        return json.toString();
    }

    /**
     * 数组转 JSON
     */
    private String arrayToJson(Object array) {
        if (array == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder("[");

        if (array instanceof Object[]) {
            Object[] objArray = (Object[]) array;
            for (int i = 0; i < objArray.length; i++) {
                if (i > 0) json.append(",");
                json.append(toJson(objArray[i]));
            }
        } else if (array instanceof int[]) {
            int[] intArray = (int[]) array;
            for (int i = 0; i < intArray.length; i++) {
                if (i > 0) json.append(",");
                json.append(intArray[i]);
            }
        } else if (array instanceof long[]) {
            long[] longArray = (long[]) array;
            for (int i = 0; i < longArray.length; i++) {
                if (i > 0) json.append(",");
                json.append(longArray[i]);
            }
        }
        // 可以添加其他基本类型数组的支持

        json.append("]");
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
