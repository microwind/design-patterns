package com.github.microwind.springai.infrastructure.config;

import com.github.microwind.springai.app.poster.PosterProperties;
import com.github.microwind.springai.app.rag.RagProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({PosterProperties.class, RagProperties.class})
public class AppConfiguration {
}
