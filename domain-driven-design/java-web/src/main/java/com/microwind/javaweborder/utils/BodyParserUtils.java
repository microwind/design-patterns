package com.microwind.javaweborder.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class BodyParserUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 解析请求体为对象
    public static <T> T parseRequestBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        BufferedReader reader = request.getReader();

        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        try {
            return objectMapper.readValue(body.toString(), clazz);
        } catch (IOException e) {
            throw new IOException("无效的 JSON 数据", e);
        }
    }
}
