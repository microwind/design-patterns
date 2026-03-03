package com.github.microwind.springai.infrastructure.config;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class PromptTemplateLoader {

    private final ResourceLoader resourceLoader;

    public PromptTemplateLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String load(String classpathLocation) {
        Resource resource = resourceLoader.getResource("classpath:" + classpathLocation);
        try (var in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load prompt: " + classpathLocation, e);
        }
    }
}
