package com.github.microwind.springai.domain.rag.port;

import com.github.microwind.springai.domain.rag.model.MediaChunkDraft;
import com.github.microwind.springai.domain.rag.model.MediaDocument;

import java.util.List;

public interface MediaKnowledgeRepository {
    long upsertDocument(MediaDocument document);

    void upsertChunks(long documentId, List<MediaChunkDraft> chunks, List<float[]> embeddings);
}
