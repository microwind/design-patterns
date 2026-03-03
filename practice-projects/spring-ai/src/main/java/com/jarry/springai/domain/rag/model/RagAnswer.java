package com.jarry.springai.domain.rag.model;

import java.util.List;

public record RagAnswer(
        String answer,
        boolean grounded,
        String fallbackReason,
        List<RetrievedChunk> evidence
) {
}
