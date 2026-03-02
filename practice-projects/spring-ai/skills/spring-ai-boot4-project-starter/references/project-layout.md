# Recommended Project Layout

```text
src/main/java/com/example/movieai/
  Application.java
  app/
    command/
    query/
    service/
  domain/
    model/
    repository/
    policy/
  infra/
    config/
    persistence/
    search/
    llm/
    crawler/
  interfaces/
    rest/
    scheduler/

src/main/resources/
  application.yml
  application-local.yml
  application-prod.yml
  db/migration/
```

## Rules

- Keep `domain` pure and independent from Spring/web/SDK details.
- Define repository and provider contracts in `domain` or `app`; implement them in `infra`.
- Keep DTOs in `interfaces/rest/dto`; avoid leaking persistence entities to API layer.
- Keep prompts in dedicated template files (`src/main/resources/prompts/*.md`) rather than inline Java strings.
