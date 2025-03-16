package com.microwind.springbootorder.config;

import com.microwind.springbootorder.common.ApiResponseWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResponseConfig {

    @Bean
    public ApiResponseWrapper apiResponseWrapper() {
        return new ApiResponseWrapper();
    }
}
