package com.github.microwind.springai.domain.poster.model;

public record MovieInfo(
        String title,
        String description,
        Integer year,
        String genre,
        String tone
) {
}
