#!/usr/bin/env python3
"""Validate movie slogan lines: 3-5 lines, each exactly 12 Chinese characters."""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

CJK_RE = re.compile(r"[\u4e00-\u9fff]")
ONLY_CJK_RE = re.compile(r"^[\u4e00-\u9fff]+$")


def read_lines(path: str | None) -> list[str]:
    if path:
        text = Path(path).read_text(encoding="utf-8")
    else:
        text = sys.stdin.read()
    return [line.strip() for line in text.splitlines() if line.strip()]


def validate(lines: list[str]) -> list[str]:
    errors: list[str] = []

    if not 3 <= len(lines) <= 5:
        errors.append(f"line count must be 3-5, got {len(lines)}")

    seen: set[str] = set()
    for idx, line in enumerate(lines, start=1):
        if not ONLY_CJK_RE.fullmatch(line):
            errors.append(f"line {idx} has non-Chinese characters: {line}")
            continue

        length = len(CJK_RE.findall(line))
        if length != 12:
            errors.append(f"line {idx} must contain 12 Chinese chars, got {length}: {line}")

        if line in seen:
            errors.append(f"line {idx} duplicated: {line}")
        seen.add(line)

    return errors


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Validate 3-5 Chinese slogan lines, each exactly 12 Chinese characters."
    )
    parser.add_argument("file", nargs="?", help="Optional text file with one slogan per line")
    args = parser.parse_args()

    lines = read_lines(args.file)
    errors = validate(lines)

    if errors:
        print("INVALID")
        for err in errors:
            print(f"- {err}")
        return 1

    print("VALID")
    for line in lines:
        print(line)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
