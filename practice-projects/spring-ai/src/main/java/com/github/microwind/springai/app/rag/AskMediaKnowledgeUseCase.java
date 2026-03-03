package com.github.microwind.springai.app.rag;

import com.github.microwind.springai.domain.rag.model.RagAnswer;
import com.github.microwind.springai.domain.rag.model.RagQuery;
import com.github.microwind.springai.domain.rag.model.RetrievedChunk;
import com.github.microwind.springai.domain.rag.policy.RetrievalAcceptancePolicy;
import com.github.microwind.springai.domain.rag.port.EmbeddingGateway;
import com.github.microwind.springai.domain.rag.port.MediaChunkRetriever;
import com.github.microwind.springai.domain.rag.port.RagAnswerGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AskMediaKnowledgeUseCase {

    private final EmbeddingGateway embeddingGateway;
    private final MediaChunkRetriever mediaChunkRetriever;
    private final RagAnswerGenerator answerGenerator;
    private final RagProperties properties;
    private final RetrievalAcceptancePolicy acceptancePolicy = new RetrievalAcceptancePolicy();

    public AskMediaKnowledgeUseCase(
            EmbeddingGateway embeddingGateway,
            MediaChunkRetriever mediaChunkRetriever,
            RagAnswerGenerator answerGenerator,
            RagProperties properties
    ) {
        this.embeddingGateway = embeddingGateway;
        this.mediaChunkRetriever = mediaChunkRetriever;
        this.answerGenerator = answerGenerator;
        this.properties = properties;
    }

    public RagAnswer execute(RagQuery query) {
        float[] queryEmbedding = embeddingGateway.embed(query.question());
        List<RetrievedChunk> retrieved = mediaChunkRetriever.retrieve(query, queryEmbedding, properties.topK());

        if (acceptancePolicy.accepted(retrieved, properties.acceptScoreThreshold(), properties.minPassingHits())) {
            String groundedAnswer = answerGenerator.generateGrounded(query, retrieved);
            return new RagAnswer(groundedAnswer, true, null, retrieved);
        }

        String fallback = answerGenerator.generateFallback(query);
        return new RagAnswer(fallback, false, "LOW_SCORE_OR_INSUFFICIENT_MATCHES", retrieved);
    }
}
