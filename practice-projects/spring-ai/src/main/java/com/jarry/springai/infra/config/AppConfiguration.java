package com.jarry.springai.infra.config;

import com.jarry.springai.app.poster.PosterProperties;
import com.jarry.springai.app.rag.RagProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({PosterProperties.class, RagProperties.class})
public class AppConfiguration {
}
