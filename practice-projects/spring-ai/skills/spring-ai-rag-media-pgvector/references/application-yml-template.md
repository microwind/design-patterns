# application-local.yml Template

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/movie_ai
    username: movie_ai
    password: movie_ai
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      embedding:
        options:
          model: text-embedding-3-small
      chat:
        options:
          model: gpt-4.1-mini
    vectorstore:
      pgvector:
        initialize-schema: false
        schema-name: public
        table-name: media_chunk

app:
  rag:
    top-k: 5
    accept-score-threshold: 0.82
    min-passing-hits: 3
    fallback-enabled: true
```

Adjust model IDs and vector dimensions together.
