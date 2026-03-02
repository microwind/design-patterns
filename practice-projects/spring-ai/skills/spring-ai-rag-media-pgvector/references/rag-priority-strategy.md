# Retrieval Priority Strategy

## Goal

Prioritize answers grounded in internal media knowledge base. Use generic model knowledge only when internal retrieval confidence is low.

## Retrieval Flow

1. Build query embedding from user question.
2. Search `media_chunk` with optional metadata filters.
3. Accept retrieval if both conditions are met:
- top-1 cosine similarity >= `0.82`
- at least `k=3` chunks above `0.75`
4. If retrieval accepted:
- assemble context from top chunks
- generate grounded answer with citations
5. If retrieval rejected:
- return fallback response
- include reason like `LOW_SCORE` or `INSUFFICIENT_MATCHES`

## SQL Template

```sql
SELECT c.id,
       c.content,
       c.metadata,
       1 - (c.embedding <=> :query_embedding) AS similarity
FROM media_chunk c
JOIN media_document d ON d.id = c.document_id
WHERE (:asset_type IS NULL OR d.asset_type = :asset_type)
  AND (:year IS NULL OR d.year = :year)
ORDER BY c.embedding <=> :query_embedding
LIMIT :k;
```

## Prompting Rule

- Inject retrieved chunks as evidence blocks.
- Ask model to answer only from evidence; if not enough evidence, say so clearly.
- Include citation ids in final answer payload.
