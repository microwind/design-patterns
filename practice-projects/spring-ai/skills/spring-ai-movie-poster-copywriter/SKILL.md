---
name: spring-ai-movie-poster-copywriter
description: Build a movie-poster one-line recommendation pipeline that takes movie title and description, gathers context from Douban and encyclopedia pages, and generates 3-5 Chinese recommendation lines with exactly 12 Chinese characters each. Use when Codex needs to implement crawler-to-prompt workflows, output validation, and recommendation generation APIs.
---

# Spring AI Movie Poster Copywriter

## Workflow

1. Collect request payload:
- Require `movieTitle` and `movieDescription`.
- Accept optional `year`, `genre`, and `tone`.

2. Retrieve context:
- Query Douban and encyclopedia pages using search first, then fetch detail pages.
- Extract only concise facts: genre, setting, themes, cast keywords, awards, and plot hooks.
- Ignore user-generated long comments and spoiler-heavy content.

3. Normalize context:
- Convert raw HTML into a compact context block under 1200 Chinese characters.
- Keep only deduplicated facts and keywords.
- Store retrieval source URLs for traceability.

4. Generate candidates:
- Load prompt templates from `references/prompt-template.md`.
- Ask model to generate 8-10 candidates first.
- Keep temperature moderate (`0.6` to `0.8`) to balance creativity and control.

5. Validate and filter:
- Run `scripts/validate_slogans.py` against candidate lines.
- Keep final 3-5 lines that satisfy length and uniqueness checks.
- If fewer than 3 survive, regenerate with stricter prompt constraints.

6. Return API response:
- Follow `references/output-schema.json`.
- Return final lines plus evidence URLs from Douban/encyclopedia retrieval.

## Guardrails

- Respect robots and rate limits; cache page fetches by URL.
- Avoid sensitive, discriminatory, or explicit wording in slogans.
- Keep language punchy and cinematic; avoid spoilers and plot twists.
- Always validate line length in code, not by model assumption.

## Resources

- `references/prompt-template.md`: system/user prompt templates.
- `references/source-extraction.md`: context extraction field rules.
- `references/output-schema.json`: API output contract.
- `scripts/validate_slogans.py`: strict 12-Chinese-character validator.
