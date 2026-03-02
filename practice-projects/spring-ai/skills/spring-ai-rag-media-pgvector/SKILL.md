---
name: spring-ai-rag-media-pgvector
description: Build RAG pipelines for media-asset knowledge bases using Spring AI and PostgreSQL pgvector. Use when Codex needs to design database schema, ingestion/chunking/embedding workflow, and retrieval logic that prioritizes internal media knowledge before falling back to general model knowledge.
---

# Spring AI RAG Media PgVector

## Workflow

1. Define knowledge scope:
- Enumerate media asset types (movie metadata, synopsis, production notes, tags, reviews, scripts).
- Define source-of-truth systems and ingestion frequency.

2. Provision database and extension:
- Apply `references/pgvector-schema.sql` via Flyway.
- Confirm `vector(1536)` dimension matches chosen embedding model.

3. Build ingestion pipeline:
- Normalize source records into JSONL chunks with stable IDs.
- Validate chunk payload using `scripts/validate_chunks.py`.
- Persist raw document metadata and vectorized chunks.

4. Embed and index:
- Use Spring AI embedding model to generate vectors.
- Upsert chunk vectors in batches.
- Rebuild IVFFlat index only after bulk ingestion.

5. Implement retrieval priority:
- Execute vector similarity search against internal chunks first.
- If top score is below threshold or result count is low, fallback to model-only answer path.
- Keep both retrieved evidence and fallback reason in response metadata.

6. Enforce grounded generation:
- Use retrieval context and source citations in prompt.
- If no reliable internal evidence is found, explicitly state uncertainty.

## Guardrails

- Keep chunk size stable (recommended 300-800 Chinese chars; overlap 50-120).
- Version embedding model IDs; avoid mixing vectors from different dimensions in one table.
- Use metadata filters (`asset_type`, `year`, `language`) before distance ranking when possible.
- Log retrieval scores for online quality monitoring.

## Resources

- `references/pgvector-schema.sql`: DB and index template for media RAG.
- `references/rag-priority-strategy.md`: retrieval-first orchestration strategy.
- `references/application-yml-template.md`: Spring AI and datasource baseline config.
- `scripts/validate_chunks.py`: chunk payload validator before embedding ingestion.
