package com.jarry.springai.interfaces.rest.rag;

import com.jarry.springai.app.rag.IngestMediaKnowledgeUseCase;
import com.jarry.springai.domain.rag.model.MediaChunkDraft;
import com.jarry.springai.domain.rag.model.MediaDocument;
import com.jarry.springai.interfaces.rest.dto.MediaIngestionRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag/media")
public class MediaIngestionController {

    private final IngestMediaKnowledgeUseCase useCase;

    public MediaIngestionController(IngestMediaKnowledgeUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/ingest")
    public ResponseEntity<Map<String, Object>> ingest(@Valid @RequestBody MediaIngestionRequest request) {
        MediaDocument document = new MediaDocument(
                request.document().sourceId(),
                request.document().title(),
                request.document().assetType(),
                request.document().language(),
                request.document().year(),
                request.document().metadata()
        );

        List<MediaChunkDraft> chunks = request.chunks().stream()
                .map(chunk -> new MediaChunkDraft(chunk.chunkNo(), chunk.content(), chunk.metadata()))
                .toList();

        useCase.execute(document, chunks);

        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "ingestedChunks", chunks.size(),
                "sourceId", document.sourceId()
        ));
    }
}
