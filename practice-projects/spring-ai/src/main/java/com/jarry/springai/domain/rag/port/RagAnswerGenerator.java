package com.jarry.springai.domain.rag.port;

import com.jarry.springai.domain.rag.model.RagQuery;
import com.jarry.springai.domain.rag.model.RetrievedChunk;

import java.util.List;

public interface RagAnswerGenerator {
    String generateGrounded(RagQuery query, List<RetrievedChunk> chunks);

    String generateFallback(RagQuery query);
}
