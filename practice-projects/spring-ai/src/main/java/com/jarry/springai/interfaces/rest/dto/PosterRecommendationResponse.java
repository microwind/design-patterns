package com.jarry.springai.interfaces.rest.dto;

import java.util.List;

public record PosterRecommendationResponse(
        String movieTitle,
        List<String> recommendations,
        List<SourceDto> sources
) {
    public record SourceDto(String site, String url) {
    }
}
