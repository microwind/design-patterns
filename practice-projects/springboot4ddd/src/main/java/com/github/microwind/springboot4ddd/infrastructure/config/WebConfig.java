package com.github.microwind.springboot4ddd.infrastructure.config;

import com.github.microwind.springboot4ddd.infrastructure.middleware.CachedBodyFilter;
import com.github.microwind.springboot4ddd.infrastructure.middleware.SignatureInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 *
 * @author jarry
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SignatureInterceptor signatureInterceptor;

    public WebConfig(SignatureInterceptor signatureInterceptor) {
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
     * 注册请求体缓存过滤器
     */
    @Bean
    public FilterRegistrationBean<CachedBodyFilter> cachedBodyFilterRegistration(CachedBodyFilter filter) {
        FilterRegistrationBean<CachedBodyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("cachedBodyFilter");
        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 签名验证拦截器
        registry.addInterceptor(signatureInterceptor)
                .addPathPatterns("/api/**")
                .order(1);
    }

    /**
     * 跨域配置
     *
     * <p>使用 {@code allowedOriginPatterns}支持模式匹配。
     * 在 {@code allowCredentials=true} 时不能使用 "*",必须列出具体来源。
     * 当前放行本机 {@code localhost} 与 {@code 127.0.0.1} 的所有端口,
     * 生产环境需收紧为具体域名。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 具体白名单配置
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
