package com.jarry.springai.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record PosterRecommendationRequest(
        @NotBlank String movieTitle,
        @NotBlank String movieDescription,
        Integer year,
        String genre,
        String tone
) {
}
