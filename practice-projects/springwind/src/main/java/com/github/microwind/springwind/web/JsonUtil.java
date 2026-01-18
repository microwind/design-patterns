package com.github.microwind.springwind.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * JSON 工具类（框架层）
 * 提供基本的 JSON 序列化和反序列化功能
 */
public final class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = createMapper();

    private JsonUtil() {
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 忽略未知字段
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    /**
     * 将 JSON 字符串解析为 Map
     * @param json JSON 字符串
     * @return Map 对象
     */
    public static Map<String, Object> parseToMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("JSON parse failed: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串解析为指定类型的对象
     * @param json JSON 字符串
     * @param clazz 目标类型
     * @return 解析后的对象
     */
    public static <T> T parseToObject(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalStateException("JSON deserialize failed: " + e.getMessage(), e);
        }
    }

    /**
     * 将对象转换为 JSON 字符串
     * @param object 要序列化的对象
     * @return JSON 字符串
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON serialize failed: " + e.getMessage(), e);
        }
    }

    /**
     * 获取 ObjectMapper 实例
     * @return ObjectMapper
     */
    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }
}
