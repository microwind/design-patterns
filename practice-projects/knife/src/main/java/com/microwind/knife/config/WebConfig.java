package com.microwind.knife.config;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.middleware.AuthInterceptor;
import com.microwind.knife.middleware.CachedBodyFilter;
import com.microwind.knife.middleware.SignatureInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final SignatureInterceptor signatureInterceptor;

    // 通过构造函数注入
    public WebConfig(AuthInterceptor authInterceptor, SignatureInterceptor signatureInterceptor) {
        this.authInterceptor = authInterceptor;
        this.signatureInterceptor = signatureInterceptor;
    }

    /**
     * 创建 CachedBodyFilter Bean
     */
    @Bean
    public CachedBodyFilter cachedBodyFilter(SignConfig signConfig) {
        return new CachedBodyFilter(signConfig);
    }

    /**
     * 注册请求体缓存过滤器（条件性）
     * <p>
     * 1. 路径级别：只匹配 /api/** 路径（与 SignatureInterceptor 保持一致）
     * 2. Header 级别：只在请求包含签名 header 时才缓存 body
     * 3. Method 级别：只有POST、PUT、PATCH 时才缓存 body
     * <p>
     * 双重过滤机制：
     * - 外层：只有 /api/** 的请求才进入 Filter
     * - 内层：只有包含签名 header 的请求才缓存 body
     * - 其他请求：完全不受影响，零性能损耗
     * <p>
     * 确保 Filter 在所有其他 Filter 和 Interceptor 之前执行
     */
    @Bean
    public FilterRegistrationBean<CachedBodyFilter> cachedBodyFilterRegistration(CachedBodyFilter filter) {
        FilterRegistrationBean<CachedBodyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        // filter属于servlet范畴，多级匹配是 /api/*，而不是 /api/**
        registration.addUrlPatterns("/api/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("cachedBodyFilter");
        return registration;
    }

    // 允许末尾斜杠匹配，并不推荐全局配置
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 签名验证拦截器（优先级高于认证拦截器）
        registry.addInterceptor(signatureInterceptor)
                .addPathPatterns("/api/**")
                .order(1);  // 优先级 1，在认证拦截器之前执行

        // 认证拦截器
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**")
                .order(2);  // 优先级 2
    }
}
