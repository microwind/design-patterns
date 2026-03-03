package com.jarry.springai.domain.rag.model;

public record RetrievedChunk(
        long chunkId,
        String sourceId,
        String documentTitle,
        String content,
        double similarity
) {
}
