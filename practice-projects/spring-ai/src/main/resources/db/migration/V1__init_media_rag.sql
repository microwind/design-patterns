CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS media_document (
  id BIGSERIAL PRIMARY KEY,
  source_id VARCHAR(128) NOT NULL,
  title VARCHAR(255) NOT NULL,
  asset_type VARCHAR(64) NOT NULL,
  language VARCHAR(32),
  year INT,
  metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (source_id)
);

CREATE TABLE IF NOT EXISTS media_chunk (
  id BIGSERIAL PRIMARY KEY,
  document_id BIGINT NOT NULL REFERENCES media_document(id) ON DELETE CASCADE,
  chunk_no INT NOT NULL,
  content TEXT NOT NULL,
  content_tokens INT,
  embedding vector(1536) NOT NULL,
  metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (document_id, chunk_no)
);

CREATE INDEX IF NOT EXISTS idx_media_document_asset_type ON media_document(asset_type);
CREATE INDEX IF NOT EXISTS idx_media_document_year ON media_document(year);
CREATE INDEX IF NOT EXISTS idx_media_chunk_document_id ON media_chunk(document_id);
CREATE INDEX IF NOT EXISTS idx_media_chunk_embedding_ivfflat
  ON media_chunk USING ivfflat (embedding vector_cosine_ops)
  WITH (lists = 100);
