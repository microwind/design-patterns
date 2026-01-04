package com.github.microwind.springboot4ddd.infrastructure.common;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一API响应对象
 *
 * @param <T> 响应数据类型
 * @author jarry
 * @since 1.0.0
 */
@Data
public class ApiResponse<T> {
    /**
     * 响应码：200-成功，其他-失败
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间
     */
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    /**
     * 成功响应（不带数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "Success", null);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 失败响应（默认400）
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(400, message, null);
    }

    /**
     * 系统错误响应
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /**
     * 未授权响应
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message, null);
    }

    /**
     * 禁止访问响应
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(403, message, null);
    }
}
