#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_DIR="$(cd -- "${SCRIPT_DIR}/.." && pwd)"
JPF_DIR="${REPO_DIR}/tools/jpf"
RUNJPF_JAR="${JPF_DIR}/build/RunJPF.jar"
SITE_PROPERTIES="${JPF_DIR}/site.properties"

if [[ $# -eq 0 ]]; then
    printf 'Usage: %s <file.jpf> [JPF options]\n' "$(basename -- "$0")" >&2
    exit 1
fi

for file in "${RUNJPF_JAR}" "${SITE_PROPERTIES}"; do
    if [[ ! -f "${file}" ]]; then
        printf 'Error: required file not found: %s\n' "${file}" >&2
        exit 1
    fi
done

# Returns the major version of the given java executable (e.g. 11, 21).
java_major() {
    "$1" -version 2>&1 | awk -F'"' '/version/ { split($2, v, "."); print (v[1] == "1" ? v[2] : v[1]); exit }'
}

# JPF (jpf-core master) runs only on Java 11. Look for a JDK 11 in this order:
# JPF_JAVA_HOME, macOS java_home, Homebrew openjdk@11, JAVA_HOME, PATH.
find_java11() {
    local candidates=()
    [[ -n "${JPF_JAVA_HOME:-}" ]] && candidates+=("${JPF_JAVA_HOME}/bin/java")
    if [[ -x /usr/libexec/java_home ]]; then
        local home
        if home="$(/usr/libexec/java_home -v 11 2>/dev/null)"; then
            candidates+=("${home}/bin/java")
        fi
    fi
    if command -v brew >/dev/null 2>&1; then
        candidates+=("$(brew --prefix openjdk@11 2>/dev/null)/bin/java")
    fi
    [[ -n "${JAVA_HOME:-}" ]] && candidates+=("${JAVA_HOME}/bin/java")
    command -v java >/dev/null 2>&1 && candidates+=("$(command -v java)")

    local java
    for java in "${candidates[@]}"; do
        if [[ -x "${java}" && "$(java_major "${java}")" == "11" ]]; then
            printf '%s\n' "${java}"
            return 0
        fi
    done
    return 1
}

if ! JAVA="$(find_java11)"; then
    printf 'Error: JPF requires Java 11, but no JDK 11 was found.\n' >&2
    printf 'Install one (e.g. "brew install openjdk@11") or set JPF_JAVA_HOME.\n' >&2
    exit 1
fi

"${JAVA}" ${JVM_FLAGS:--Xmx1024m -ea} -jar "${RUNJPF_JAR}" "+site=${SITE_PROPERTIES}" "$@"
