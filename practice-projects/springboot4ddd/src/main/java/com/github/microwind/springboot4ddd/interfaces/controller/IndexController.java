package com.github.microwind.springboot4ddd.interfaces.controller;

import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认页面
 *
 * @author jarry
 * @since 1.0.0
 */
@RestController
@RequestMapping("")
public class IndexController {
    /**
     * 首页接口
     *
     * @return 欢迎信息
     */
    @GetMapping("/")
    public ApiResponse<String> index() {
        return ApiResponse.success("Welcome to Spring Boot 4 DDD Project!");
    }
}
