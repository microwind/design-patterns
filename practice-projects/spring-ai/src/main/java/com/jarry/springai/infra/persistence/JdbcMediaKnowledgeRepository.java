package com.jarry.springai.infra.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jarry.springai.domain.rag.model.MediaChunkDraft;
import com.jarry.springai.domain.rag.model.MediaDocument;
import com.jarry.springai.domain.rag.port.MediaKnowledgeRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcMediaKnowledgeRepository implements MediaKnowledgeRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public JdbcMediaKnowledgeRepository(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public long upsertDocument(MediaDocument document) {
        String sql = """
                INSERT INTO media_document(source_id, title, asset_type, language, year, metadata)
                VALUES (:sourceId, :title, :assetType, :language, :year, CAST(:metadata AS jsonb))
                ON CONFLICT(source_id) DO UPDATE SET
                    title = EXCLUDED.title,
                    asset_type = EXCLUDED.asset_type,
                    language = EXCLUDED.language,
                    year = EXCLUDED.year,
                    metadata = EXCLUDED.metadata,
                    updated_at = NOW()
                RETURNING id
                """;

        return jdbcClient.sql(sql)
                .param("sourceId", document.sourceId())
                .param("title", document.title())
                .param("assetType", document.assetType())
                .param("language", document.language())
                .param("year", document.year())
                .param("metadata", toJson(document.metadata()))
                .query(Long.class)
                .single();
    }

    @Override
    public void upsertChunks(long documentId, List<MediaChunkDraft> chunks, List<float[]> embeddings) {
        if (chunks.size() != embeddings.size()) {
            throw new IllegalArgumentException("chunks and embeddings size mismatch");
        }

        String sql = """
                INSERT INTO media_chunk(document_id, chunk_no, content, content_tokens, embedding, metadata)
                VALUES (:documentId, :chunkNo, :content, :contentTokens, CAST(:embedding AS vector), CAST(:metadata AS jsonb))
                ON CONFLICT(document_id, chunk_no) DO UPDATE SET
                    content = EXCLUDED.content,
                    content_tokens = EXCLUDED.content_tokens,
                    embedding = EXCLUDED.embedding,
                    metadata = EXCLUDED.metadata
                """;

        for (int i = 0; i < chunks.size(); i++) {
            MediaChunkDraft chunk = chunks.get(i);
            float[] embedding = embeddings.get(i);
            jdbcClient.sql(sql)
                    .param("documentId", documentId)
                    .param("chunkNo", chunk.chunkNo())
                    .param("content", chunk.content())
                    .param("contentTokens", chunk.content().length())
                    .param("embedding", PgVectorSqlUtils.toVectorLiteral(embedding))
                    .param("metadata", toJson(chunk.metadata()))
                    .update();
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? java.util.Map.of() : value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize metadata", e);
        }
    }
}
