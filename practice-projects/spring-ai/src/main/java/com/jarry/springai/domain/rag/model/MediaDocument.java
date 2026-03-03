package com.jarry.springai.domain.rag.model;

import java.util.Map;

public record MediaDocument(
        String sourceId,
        String title,
        String assetType,
        String language,
        Integer year,
        Map<String, Object> metadata
) {
}
