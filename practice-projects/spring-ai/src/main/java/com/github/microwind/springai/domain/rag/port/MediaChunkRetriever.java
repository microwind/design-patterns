package com.github.microwind.springai.domain.rag.port;

import com.github.microwind.springai.domain.rag.model.RagQuery;
import com.github.microwind.springai.domain.rag.model.RetrievedChunk;

import java.util.List;

public interface MediaChunkRetriever {
    List<RetrievedChunk> retrieve(RagQuery query, float[] queryEmbedding, int topK);
}
