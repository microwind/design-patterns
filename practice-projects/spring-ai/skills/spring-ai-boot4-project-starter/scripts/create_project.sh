#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage:
  create_project.sh <groupId> <artifactId> [outputDir]

Environment variables:
  BOOT_VERSION   Spring Boot version (default: 4.0.3)
  JAVA_VERSION   Java version (default: 21)
  STARTER_URL    Spring Initializr URL (default: https://start.spring.io/starter.zip)
  DEPENDENCIES   Initializr deps list (default: web,validation,actuator,lombok,postgresql,flyway)
USAGE
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
  usage
  exit 0
fi

if [[ $# -lt 2 ]]; then
  usage
  exit 1
fi

GROUP_ID="$1"
ARTIFACT_ID="$2"
OUTPUT_DIR="${3:-$PWD}"

BOOT_VERSION="${BOOT_VERSION:-4.0.3}"
JAVA_VERSION="${JAVA_VERSION:-21}"
STARTER_URL="${STARTER_URL:-https://start.spring.io/starter.zip}"
DEPENDENCIES="${DEPENDENCIES:-web,validation,actuator,lombok,postgresql,flyway}"

TARGET_DIR="${OUTPUT_DIR%/}/${ARTIFACT_ID}"
TMP_ZIP="$(mktemp /tmp/${ARTIFACT_ID}.XXXXXX.zip)"

cleanup() {
  rm -f "$TMP_ZIP"
}
trap cleanup EXIT

mkdir -p "$TARGET_DIR"

curl -fsSL "$STARTER_URL" \
  -d type=maven-project \
  -d language=java \
  -d bootVersion="$BOOT_VERSION" \
  -d javaVersion="$JAVA_VERSION" \
  -d groupId="$GROUP_ID" \
  -d artifactId="$ARTIFACT_ID" \
  -d name="$ARTIFACT_ID" \
  -d packageName="${GROUP_ID}.${ARTIFACT_ID}" \
  -d dependencies="$DEPENDENCIES" \
  -o "$TMP_ZIP"

unzip -q "$TMP_ZIP" -d "$TARGET_DIR"

cat > "${TARGET_DIR}/SPRING_AI_NEXT_STEPS.md" <<'DOC'
# Next Steps

1. Add Spring AI BOM and provider starter dependencies.
2. Add vector store dependency for your target database.
3. Configure `application-local.yml` with API key and DB config.
4. Create first smoke endpoint and run `./mvnw test`.
DOC

echo "Project created at: ${TARGET_DIR}"
