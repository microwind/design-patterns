package com.microwind.springbootorder.common;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 这里返回 true，表示对所有控制器的方法都应用此 Advice
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 仅当请求路径以 "/api/" 开头时，才进行包装
        if (request.getURI().getPath().startsWith("/api/")) {
            // 如果响应体是 ApiResponse 类型，直接返回
            if (body instanceof ApiResponse) {
                return body;
            }

            // 统一将所有非 ApiResponse 类型的返回值都包装成 ApiResponse
            return new ApiResponse<>(200, body, "获取成功");
        }

        // 否则，直接返回原始的响应体
        return body;
    }
}