package com.github.microwind.userdemo.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * JSON 工具类
 */
public final class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = createMapper();

    private JsonUtil() {
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 忽略未知字段
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 空值不输出
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }

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

    public static Map<String, Object> parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("JSON parse failed: " + e.getMessage(), e);
        }
    }

    public static <T> T parseJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new IllegalStateException("JSON deserialize failed: " + e.getMessage(), e);
        }
    }

    public static <T> T parseJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new IllegalStateException("JSON deserialize failed: " + e.getMessage(), e);
        }
    }

    public static <T> T parseJson(String json, JavaType javaType) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw new IllegalStateException("JSON deserialize failed: " + e.getMessage(), e);
        }
    }

    public static ObjectMapper mapper() {
        return OBJECT_MAPPER;
    }

    public static JavaType constructCollectionType(Class<? extends Collection> collectionClass,
                                                   Class<?> elementClass) {
        return OBJECT_MAPPER.getTypeFactory()
                .constructCollectionType(collectionClass, elementClass);
    }
}