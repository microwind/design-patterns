package com.github.microwind.userdemo.utils;

import com.github.microwind.springwind.web.ViewResult;
import com.github.microwind.springwind.web.JsonResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一 API 响应封装类
 * 提供便捷的静态方法创建成功和失败响应
 * 实现 ViewResult 接口，可以直接作为控制器返回值
 */
public class ApiResponse<T> implements ViewResult {
    private int code;
    private T data;
    private String message;

    public ApiResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // -------------------- 成功响应 --------------------

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, data, message);
    }

    /**
     * 成功响应（默认消息）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data, "操作成功");
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message);
    }

    /**
     * 成功响应（默认消息，无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功");
    }

    // -------------------- 失败响应 --------------------

    /**
     * 失败响应（自定义状态码）
     */
    public static <T> ApiResponse<T> failure(int code, String message) {
        return new ApiResponse<>(code, message);
    }

    /**
     * 失败响应（默认500错误）
     */
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(500, message);
    }

    /**
     * 400 错误请求
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message);
    }

    /**
     * 404 未找到
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message);
    }

    /**
     * 401 未授权
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message);
    }

    // -------------------- 分页响应 --------------------

    /**
     * 分页响应（使用 PageResult 对象）
     *
     * @param pageResult 分页结果对象
     * @param <T>        数据类型
     * @return ApiResponse 响应对象
     */
    public static <T> ApiResponse<PageResult<T>> page(PageResult<T> pageResult) {
        return new ApiResponse<>(200, pageResult, "获取列表成功");
    }

    /**
     * 分页响应（便捷方法）
     *
     * @param list     数据列表
     * @param page     当前页码
     * @param pageSize 每页大小
     * @param total    总记录数
     * @param <T>      数据类型
     * @return ApiResponse 响应对象
     */
    public static <T> ApiResponse<PageResult<T>> page(
            java.util.List<T> list,
            int page,
            int pageSize,
            long total) {
        PageResult<T> pageResult = PageResult.of(list, page, pageSize, total);
        return new ApiResponse<>(200, pageResult, "获取列表成功");
    }

    /**
     * 分页响应（自定义消息）
     *
     * @param pageResult 分页结果对象
     * @param message    响应消息
     * @param <T>        数据类型
     * @return ApiResponse 响应对象
     */
    public static <T> ApiResponse<PageResult<T>> page(PageResult<T> pageResult, String message) {
        return new ApiResponse<>(200, pageResult, message);
    }

    // -------------------- Getters --------------------

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 转换为 Map，用于 JsonResult
     * 使用 Jackson 确保对象能正确序列化
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        if (data != null) {
            // 使用 Jackson 将对象转换为 Map/List，确保嵌套对象也能正确序列化
            try {
                String json = JsonUtil.toJson(data);
                Object parsedData = JsonUtil.parseJson(json, Object.class);
                map.put("data", parsedData);
            } catch (Exception e) {
                // 如果序列化失败，直接放入原对象
                map.put("data", data);
            }
        }
        return map;
    }

    // -------------------- ViewResult 实现 --------------------

    /**
     * 实现 ViewResult 接口，将响应渲染为 JSON
     */
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JsonResult jsonResult = new JsonResult(this.toMap());
        jsonResult.render(request, response);
    }
}