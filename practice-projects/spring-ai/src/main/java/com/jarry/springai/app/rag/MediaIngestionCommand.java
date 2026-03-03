package com.jarry.springai.app.rag;

import com.jarry.springai.domain.rag.model.MediaChunkDraft;
import com.jarry.springai.domain.rag.model.MediaDocument;

import java.util.List;

public record MediaIngestionCommand(MediaDocument document, List<MediaChunkDraft> chunks) {
}
