package com.github.microwind.springwind.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 视图结果接口 - 允许应用自定义响应处理
 */
public interface ViewResult {
    /**
     * 渲染视图结果到响应中
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @throws Exception 渲染异常
     */
    void render(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
