package com.jarry.springai.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

public record MediaIngestionRequest(
        @Valid DocumentDto document,
        @NotEmpty List<@Valid ChunkDto> chunks
) {
    public record DocumentDto(
            @NotBlank String sourceId,
            @NotBlank String title,
            @NotBlank String assetType,
            String language,
            Integer year,
            Map<String, Object> metadata
    ) {
    }

    public record ChunkDto(
            int chunkNo,
            @NotBlank String content,
            Map<String, Object> metadata
    ) {
    }
}
