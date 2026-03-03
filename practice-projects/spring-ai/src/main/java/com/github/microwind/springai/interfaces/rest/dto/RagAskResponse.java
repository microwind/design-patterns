package com.github.microwind.springai.interfaces.rest.dto;

import java.util.List;

public record RagAskResponse(
        String answer,
        boolean grounded,
        String fallbackReason,
        List<EvidenceDto> evidence
) {
    public record EvidenceDto(long chunkId, String sourceId, String documentTitle, double similarity) {
    }
}
