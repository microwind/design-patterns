package com.jarry.springai.infra.llm;

import com.jarry.springai.domain.rag.port.EmbeddingGateway;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

@Component
public class SpringAiEmbeddingGateway implements EmbeddingGateway {

    private final EmbeddingModel embeddingModel;

    public SpringAiEmbeddingGateway(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public float[] embed(String text) {
        return embeddingModel.embed(text);
    }
}
