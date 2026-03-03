package com.jarry.springai.app.rag;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rag")
public record RagProperties(int topK, double acceptScoreThreshold, int minPassingHits) {
}
