package com.github.microwind.springai.domain.rag.policy;

import com.github.microwind.springai.domain.rag.model.RetrievedChunk;

import java.util.List;

public class RetrievalAcceptancePolicy {

    public boolean accepted(List<RetrievedChunk> chunks, double threshold, int minPassingHits) {
        if (chunks == null || chunks.isEmpty()) {
            return false;
        }

        long passingHits = chunks.stream().filter(chunk -> chunk.similarity() >= threshold).count();
        return chunks.getFirst().similarity() >= threshold && passingHits >= minPassingHits;
    }
}
