package com.github.microwind.springai.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RagAskRequest(
        @NotBlank String question,
        String assetType,
        Integer year
) {
}
