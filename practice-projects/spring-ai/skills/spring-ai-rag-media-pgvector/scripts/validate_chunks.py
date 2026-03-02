#!/usr/bin/env python3
"""Validate JSONL chunk data before embedding ingestion."""

from __future__ import annotations

import argparse
import json
from pathlib import Path

REQUIRED_KEYS = {"source_id", "chunk_no", "content"}
MAX_CONTENT_LEN = 1600


def validate_line(payload: dict, line_no: int) -> list[str]:
    errors: list[str] = []

    missing = REQUIRED_KEYS - payload.keys()
    if missing:
        errors.append(f"line {line_no}: missing keys {sorted(missing)}")
        return errors

    if not isinstance(payload["source_id"], str) or not payload["source_id"].strip():
        errors.append(f"line {line_no}: source_id must be non-empty string")

    if not isinstance(payload["chunk_no"], int) or payload["chunk_no"] < 0:
        errors.append(f"line {line_no}: chunk_no must be non-negative int")

    content = payload["content"]
    if not isinstance(content, str) or not content.strip():
        errors.append(f"line {line_no}: content must be non-empty string")
    elif len(content) > MAX_CONTENT_LEN:
        errors.append(
            f"line {line_no}: content too long ({len(content)} > {MAX_CONTENT_LEN})"
        )

    return errors


def main() -> int:
    parser = argparse.ArgumentParser(description="Validate media chunk JSONL file")
    parser.add_argument("file", help="Path to JSONL chunk file")
    args = parser.parse_args()

    lines = Path(args.file).read_text(encoding="utf-8").splitlines()

    all_errors: list[str] = []
    for i, line in enumerate(lines, start=1):
        if not line.strip():
            continue
        try:
            payload = json.loads(line)
        except json.JSONDecodeError as exc:
            all_errors.append(f"line {i}: invalid json ({exc})")
            continue
        all_errors.extend(validate_line(payload, i))

    if all_errors:
        print("INVALID")
        for err in all_errors:
            print(f"- {err}")
        return 1

    print(f"VALID ({len([l for l in lines if l.strip()])} records)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
