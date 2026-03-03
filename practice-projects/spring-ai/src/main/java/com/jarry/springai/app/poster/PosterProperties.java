package com.jarry.springai.app.poster;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.poster")
public record PosterProperties(int candidateCount, int minFinalCount, int maxFinalCount) {
}
