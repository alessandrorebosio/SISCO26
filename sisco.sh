#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
APP_DIR="${SCRIPT_DIR}/app"
LTL2BUCHI_JAR="${APP_DIR}/ltl2buchi.jar"
LTSA_JAR="${APP_DIR}/ltsa.jar"

if ! command -v java >/dev/null 2>&1; then
    printf 'Error: Java is required but was not found in PATH.\n' >&2
    exit 1
fi

for jar in "${LTL2BUCHI_JAR}" "${LTSA_JAR}"; do
    if [[ ! -f "${jar}" ]]; then
        printf 'Error: required file not found: %s\n' "${jar}" >&2
        exit 1
    fi
done

# LTL2Buchi is a runtime module of LTSA and must be on its classpath.
java -cp "${LTSA_JAR}:${LTL2BUCHI_JAR}" HPWindow
