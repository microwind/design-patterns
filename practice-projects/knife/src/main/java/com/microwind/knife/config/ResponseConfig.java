package com.microwind.knife.config;

import com.microwind.knife.common.ApiResponseWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResponseConfig {

    @Bean
    public ApiResponseWrapper apiResponseWrapper() {
        return new ApiResponseWrapper();
    }
}
