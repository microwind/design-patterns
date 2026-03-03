package com.github.microwind.springai.infrastructure.persistence;

import com.github.microwind.springai.domain.rag.model.RagQuery;
import com.github.microwind.springai.domain.rag.model.RetrievedChunk;
import com.github.microwind.springai.domain.rag.port.MediaChunkRetriever;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcMediaChunkRetriever implements MediaChunkRetriever {

    private final JdbcClient jdbcClient;

    public JdbcMediaChunkRetriever(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<RetrievedChunk> retrieve(RagQuery query, float[] queryEmbedding, int topK) {
        String sql = """
                SELECT c.id AS chunk_id,
                       d.source_id,
                       d.title,
                       c.content,
                       1 - (c.embedding <=> CAST(:embedding AS vector)) AS similarity
                FROM media_chunk c
                JOIN media_document d ON d.id = c.document_id
                WHERE (:assetType IS NULL OR d.asset_type = :assetType)
                  AND (:year IS NULL OR d.year = :year)
                ORDER BY c.embedding <=> CAST(:embedding AS vector)
                LIMIT :topK
                """;

        return jdbcClient.sql(sql)
                .param("embedding", PgVectorSqlUtils.toVectorLiteral(queryEmbedding))
                .param("assetType", blankToNull(query.assetType()))
                .param("year", query.year())
                .param("topK", topK)
                .query((rs, rowNum) -> new RetrievedChunk(
                        rs.getLong("chunk_id"),
                        rs.getString("source_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getDouble("similarity")
                ))
                .list();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
