package com.jarry.springai.domain.rag.model;

import java.util.Map;

public record MediaChunkDraft(
        int chunkNo,
        String content,
        Map<String, Object> metadata
) {
}
