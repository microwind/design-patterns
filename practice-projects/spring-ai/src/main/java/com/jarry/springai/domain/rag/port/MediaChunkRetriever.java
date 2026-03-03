package com.jarry.springai.domain.rag.port;

import com.jarry.springai.domain.rag.model.RagQuery;
import com.jarry.springai.domain.rag.model.RetrievedChunk;

import java.util.List;

public interface MediaChunkRetriever {
    List<RetrievedChunk> retrieve(RagQuery query, float[] queryEmbedding, int topK);
}
