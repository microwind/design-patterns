package com.github.microwind.springai.app.rag;

import com.github.microwind.springai.domain.rag.model.MediaChunkDraft;
import com.github.microwind.springai.domain.rag.model.MediaDocument;

import java.util.List;

public record MediaIngestionCommand(MediaDocument document, List<MediaChunkDraft> chunks) {
}
