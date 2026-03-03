package com.jarry.springai.domain.poster.model;

import java.util.List;

public record PosterRecommendationResult(
        String movieTitle,
        List<String> recommendations,
        List<SourceReference> sources
) {
}
