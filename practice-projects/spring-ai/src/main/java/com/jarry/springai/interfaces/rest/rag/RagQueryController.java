package com.jarry.springai.interfaces.rest.rag;

import com.jarry.springai.app.rag.AskMediaKnowledgeUseCase;
import com.jarry.springai.domain.rag.model.RagAnswer;
import com.jarry.springai.domain.rag.model.RagQuery;
import com.jarry.springai.interfaces.rest.dto.RagAskRequest;
import com.jarry.springai.interfaces.rest.dto.RagAskResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rag")
public class RagQueryController {

    private final AskMediaKnowledgeUseCase useCase;

    public RagQueryController(AskMediaKnowledgeUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/ask")
    public RagAskResponse ask(@Valid @RequestBody RagAskRequest request) {
        RagAnswer answer = useCase.execute(new RagQuery(request.question(), request.assetType(), request.year()));

        List<RagAskResponse.EvidenceDto> evidence = answer.evidence().stream()
                .map(chunk -> new RagAskResponse.EvidenceDto(
                        chunk.chunkId(),
                        chunk.sourceId(),
                        chunk.documentTitle(),
                        chunk.similarity()
                ))
                .toList();

        return new RagAskResponse(answer.answer(), answer.grounded(), answer.fallbackReason(), evidence);
    }
}
