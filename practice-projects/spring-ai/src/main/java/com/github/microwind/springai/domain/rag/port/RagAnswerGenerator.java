package com.github.microwind.springai.domain.rag.port;

import com.github.microwind.springai.domain.rag.model.RagQuery;
import com.github.microwind.springai.domain.rag.model.RetrievedChunk;

import java.util.List;

public interface RagAnswerGenerator {
    String generateGrounded(RagQuery query, List<RetrievedChunk> chunks);

    String generateFallback(RagQuery query);
}
