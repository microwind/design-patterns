package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.common.ApiResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ApiResponse<Map<String, Object>> handleError(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Map<String, Object> body = new HashMap<>();
        body.put("code", status.value());
        body.put("title", "Web Server Parsing Error");
        body.put("detail", "请求出现错误，请检查请求地址与参数。");

        return ApiResponse.success(body, "请求错误。");
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        return HttpStatus.valueOf(statusCode);
    }
}