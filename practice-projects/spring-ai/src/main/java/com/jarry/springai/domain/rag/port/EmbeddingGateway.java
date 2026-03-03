package com.jarry.springai.domain.rag.port;

public interface EmbeddingGateway {
    float[] embed(String text);
}
