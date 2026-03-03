package com.github.microwind.springai.domain.rag.port;

public interface EmbeddingGateway {
    float[] embed(String text);
}
