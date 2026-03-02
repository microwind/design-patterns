---
name: spring-ai-boot4-project-starter
description: Bootstrap and standardize Spring Boot 4.0.3 + Spring AI backend projects for new implementations. Use when Codex needs to initialize a project, set Maven dependencies/BOM, define package and module layout, configure environment profiles, or create baseline API/AI/database scaffolding.
---

# Spring AI Boot4 Project Starter

## Workflow

1. Confirm baseline inputs before coding:
- `groupId`, `artifactId`, Java version (default 21), build tool (default Maven), and deployment target.

2. Initialize project skeleton:
- Run `scripts/create_project.sh <groupId> <artifactId> [outputDir]`.
- If Spring Initializr is unavailable, create the same structure manually with `pom.xml`, `src/main`, `src/test`, and profile-specific config files.

3. Apply dependency baseline:
- Read `references/dependency-baseline.md` and add the Spring AI BOM plus required starters.
- Keep dependencies minimal first; add vector-store provider and crawler libraries only when needed.

4. Apply directory and package standards:
- Read `references/project-layout.md`.
- Create `app` (application services), `domain` (entities/rules), `infra` (DB/search/model providers), and `interfaces` (REST/scheduled jobs).

5. Configure runtime baseline:
- Create `application.yml`, `application-local.yml`, `application-prod.yml`.
- Wire DB migration (Flyway) and health checks (`/actuator/health`, readiness/liveness).

6. Add smoke tests:
- Add one context-load test and one API contract test.
- Ensure project builds with `./mvnw -q test` before adding feature code.

## Guardrails

- Keep Spring AI provider and embedding model IDs externalized in config.
- Keep feature modules independent from provider SDK classes; isolate provider binding in `infra`.
- Generate deterministic prompts from templates; do not hard-code prompt text in controllers.
- Version SQL changes through Flyway migration files only.

## Resources

- `scripts/create_project.sh`: Boot 4.0.3 skeleton generator.
- `references/dependency-baseline.md`: Maven dependency and BOM baseline.
- `references/project-layout.md`: package/module conventions.
