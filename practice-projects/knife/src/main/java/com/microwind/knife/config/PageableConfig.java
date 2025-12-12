package com.microwind.knife.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class PageableConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver resolver =
                new PageableHandlerMethodArgumentResolver();

        resolver.setOneIndexedParameters(true); // 关键：让 page=1 表示第一页

        resolvers.add(resolver);
    }
}
