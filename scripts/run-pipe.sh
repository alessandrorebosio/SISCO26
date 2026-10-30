#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
PIPE_JAR="$(cd -- "${SCRIPT_DIR}/../tools/pipe" && pwd)/pipe.jar"

if ! command -v java >/dev/null 2>&1; then
    printf 'Error: Java is required but was not found in PATH.\n' >&2
    exit 1
fi

if [[ ! -f "${PIPE_JAR}" ]]; then
    printf 'Error: required file not found: %s\n' "${PIPE_JAR}" >&2
    exit 1
fi

java -jar "${PIPE_JAR}" "$@"