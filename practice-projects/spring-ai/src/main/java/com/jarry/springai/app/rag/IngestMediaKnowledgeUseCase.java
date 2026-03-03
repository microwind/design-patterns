package com.jarry.springai.app.rag;

import com.jarry.springai.domain.rag.model.MediaChunkDraft;
import com.jarry.springai.domain.rag.model.MediaDocument;
import com.jarry.springai.domain.rag.port.EmbeddingGateway;
import com.jarry.springai.domain.rag.port.MediaKnowledgeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngestMediaKnowledgeUseCase {

    private final MediaKnowledgeRepository repository;
    private final EmbeddingGateway embeddingGateway;

    public IngestMediaKnowledgeUseCase(MediaKnowledgeRepository repository, EmbeddingGateway embeddingGateway) {
        this.repository = repository;
        this.embeddingGateway = embeddingGateway;
    }

    public void execute(MediaDocument document, List<MediaChunkDraft> chunks) {
        long documentId = repository.upsertDocument(document);
        List<float[]> embeddings = chunks.stream()
                .map(MediaChunkDraft::content)
                .map(embeddingGateway::embed)
                .toList();

        repository.upsertChunks(documentId, chunks, embeddings);
    }
}
