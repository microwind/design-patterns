package com.microwind.springai.domain.rag;

import com.github.microwind.springai.domain.rag.model.RetrievedChunk;
import com.github.microwind.springai.domain.rag.policy.RetrievalAcceptancePolicy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetrievalAcceptancePolicyTest {

    private final RetrievalAcceptancePolicy policy = new RetrievalAcceptancePolicy();

    @Test
    void shouldAcceptWhenThresholdAndHitsSatisfied() {
        List<RetrievedChunk> chunks = List.of(
                new RetrievedChunk(1L, "s1", "t1", "c1", 0.90),
                new RetrievedChunk(2L, "s1", "t1", "c2", 0.86),
                new RetrievedChunk(3L, "s1", "t1", "c3", 0.83)
        );

        assertTrue(policy.accepted(chunks, 0.82, 3));
    }

    @Test
    void shouldRejectWhenTopScoreTooLow() {
        List<RetrievedChunk> chunks = List.of(
                new RetrievedChunk(1L, "s1", "t1", "c1", 0.70),
                new RetrievedChunk(2L, "s1", "t1", "c2", 0.69),
                new RetrievedChunk(3L, "s1", "t1", "c3", 0.68)
        );

        assertFalse(policy.accepted(chunks, 0.82, 2));
    }
}
