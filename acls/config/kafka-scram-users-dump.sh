#!/usr/bin/env bash
set -euo pipefail

# kafka-scram-users-dump.sh
# List Kafka users that have SCRAM credentials (SCRAM-SHA-256 / SCRAM-SHA-512)
# via Admin API using kafka-configs.sh --describe --entity-type users.
# Outputs CSV: user,mechanism,iterations
#
# Requirements:
# - bash
# - awk (POSIX/BSD or GNU) — parser is POSIX compatible
# - kafka-configs.sh available in PATH or via --kafka-bin-dir

OUTPUT="csv"                 # csv | plain
TRUSTSTORE_TYPE="JKS"        # JKS | PKCS12
KEYSTORE_TYPE=""             # JKS | PKCS12 (optional)
CLIENT_PROPS=""              # optional: external client.properties (overrides TLS flags)
KAFKA_BIN_DIR=""             # optional path to Kafka bin dir

usage() {
  cat <<EOF
Usage: $0 --bootstrap-server host:9093 \\
          [--command-config /path/client.properties] \\
          [--truststore /path/truststore.jks --truststore-password 'pass' --truststore-type JKS|PKCS12] \\
          [--keystore /path/keystore.jks --keystore-password 'pass' --keystore-type JKS|PKCS12] \\
          [--output csv|plain] [--kafka-bin-dir /path/to/kafka/bin]

Notes:
- This lists users that have SCRAM credentials configured; passwords are not retrievable.
- If --command-config is provided, TLS flags here are ignored in favor of that file.
- CSV columns: user,mechanism,iterations
EOF
}

BOOTSTRAP=""
TRUSTSTORE=""
TRUSTSTORE_PASS=""
KEYSTORE=""
KEYSTORE_PASS=""

# Parse args
while [[ $# -gt 0 ]]; do
  case "$1" in
    --bootstrap-server) BOOTSTRAP="${2:-}"; shift 2;;
    --command-config)   CLIENT_PROPS="${2:-}"; shift 2;;
    --truststore)       TRUSTSTORE="${2:-}"; shift 2;;
    --truststore-password) TRUSTSTORE_PASS="${2:-}"; shift 2;;
    --truststore-type)  TRUSTSTORE_TYPE="${2:-}"; shift 2;;
    --keystore)         KEYSTORE="${2:-}"; shift 2;;
    --keystore-password) KEYSTORE_PASS="${2:-}"; shift 2;;
    --keystore-type)    KEYSTORE_TYPE="${2:-}"; shift 2;;
    --output)           OUTPUT="${2:-}"; shift 2;;
    --kafka-bin-dir)    KAFKA_BIN_DIR="${2:-}"; shift 2;;
    -h|--help)          usage; exit 0;;
    *) echo "Unknown arg: $1" >&2; usage; exit 1;;
  esac
done

if [[ -z "${BOOTSTRAP}" ]]; then
  echo "ERROR: --bootstrap-server is required" >&2
  usage
  exit 1
fi

# Resolve kafka-configs.sh
KAFKA_CONFIGS="kafka-configs.sh"
if [[ -n "${KAFKA_BIN_DIR}" ]]; then
  KAFKA_CONFIGS="${KAFKA_BIN_DIR%/}/kafka-configs.sh"
fi
if ! command -v "${KAFKA_CONFIGS}" >/dev/null 2>&1; then
  echo "ERROR: ${KAFKA_CONFIGS} not found in PATH" >&2
  exit 1
fi

# If no external client.properties, require truststore + password to build one
if [[ -z "${CLIENT_PROPS}" ]]; then
  if [[ -z "${TRUSTSTORE}" || -z "${TRUSTSTORE_PASS}" ]]; then
    echo "ERROR: Either provide --command-config or both --truststore and --truststore-password" >&2
    usage
    exit 1
  fi
fi

# Build a temporary client.properties if not supplied
TMP_CLIENT_PROPS=""
cleanup() {
  if [[ -n "${TMP_CLIENT_PROPS}" && -f "${TMP_CLIENT_PROPS}" ]]; then
    rm -f "${TMP_CLIENT_PROPS}"
  fi
}
trap cleanup EXIT

if [[ -z "${CLIENT_PROPS}" ]]; then
  TMP_CLIENT_PROPS="$(mktemp)"
  chmod 600 "${TMP_CLIENT_PROPS}"
  {
    echo "security.protocol=SSL"
    echo "ssl.truststore.location=${TRUSTSTORE}"
    echo "ssl.truststore.password=${TRUSTSTORE_PASS}"
    echo "ssl.truststore.type=${TRUSTSTORE_TYPE}"
    if [[ -n "${KEYSTORE}" ]]; then
      echo "ssl.keystore.location=${KEYSTORE}"
      echo "ssl.keystore.password=${KEYSTORE_PASS}"
      if [[ -n "${KEYSTORE_TYPE}" ]]; then
        echo "ssl.keystore.type=${KEYSTORE_TYPE}"
      fi
      # If your private key password differs, you may also need:
      # echo "ssl.key.password=yourKeyPassword"
    fi
  } > "${TMP_CLIENT_PROPS}"
  CLIENT_PROPS="${TMP_CLIENT_PROPS}"
fi

# Describe all users (different Kafka versions format slightly differ, handle both)
set +e
RAW_OUT="$("${KAFKA_CONFIGS}" --bootstrap-server "${BOOTSTRAP}" --command-config "${CLIENT_PROPS}" --describe --entity-type users 2>&1)"
KAFKA_RC=$?
set -e

if [[ $KAFKA_RC -ne 0 && -z "${RAW_OUT}" ]]; then
  echo "ERROR: kafka-configs.sh failed (rc=${KAFKA_RC})" >&2
  exit $KAFKA_RC
fi

if [[ -z "${RAW_OUT}" ]]; then
  echo "No user configs returned (or insufficient authorization)."
  exit 0
fi

if [[ "${OUTPUT}" == "plain" ]]; then
  printf "%s\n" "${RAW_OUT}"
  exit 0
fi

# POSIX-compatible CSV parsing.
# Matches lines like:
#   Dynamic configs for user-principal 'alice' are: SCRAM-SHA-256=[iterations=4096], SCRAM-SHA-512=[iterations=4096]
#   Configs for user-principal 'bob' are SCRAM-SHA-512=[iterations=4096]
# Ignores default user-principal '*'
#
# Outputs:
#   user,mechanism,iterations
awk -v OFS=',' '
  function trim(s) { gsub(/^[[:space:]]+|[[:space:]]+$/, "", s); return s }
  function extract_user(line,   p, start, rest, endq) {
    p = index(line, "user-principal '\''")
    if (p == 0) return ""
    start = p + length("user-principal '\''")
    rest = substr(line, start)
    endq = index(rest, "'\''")
    if (endq == 0) return ""
    return substr(rest, 1, endq-1)
  }
  function extract_iterations(chunk,   pos, start, digits, i, c) {
    pos = index(chunk, "iterations=")
    if (pos == 0) return ""
    start = pos + length("iterations=")
    digits = ""
    for (i = start; i <= length(chunk); i++) {
      c = substr(chunk, i, 1)
      if (c ~ /[0-9]/) { digits = digits c } else { break }
    }
    return digits
  }

  BEGIN {
    print "user","mechanism","iterations"
  }

  # Lines describing user configs; accept with or without colon after "are"
  /configs for user-principal/ {
    user = extract_user($0)
    if (user == "" || user == "*") next

    line = $0

    # For each mechanism, if present, extract iterations
    # SCRAM-SHA-256
    pos = index(line, "SCRAM-SHA-256=")
    if (pos > 0) {
      sub256 = substr(line, pos)
      b1 = index(sub256, "["); b2 = index(sub256, "]")
      conf = (b1>0 && b2>b1) ? substr(sub256, b1+1, b2-b1-1) : ""
      iters = extract_iterations(conf)
      print user, "SCRAM-SHA-256", iters
    }

    # SCRAM-SHA-512
    pos2 = index(line, "SCRAM-SHA-512=")
    if (pos2 > 0) {
      sub512 = substr(line, pos2)
      c1 = index(sub512, "["); c2 = index(sub512, "]")
      conf2 = (c1>0 && c2>c1) ? substr(sub512, c1+1, c2-c1-1) : ""
      iters2 = extract_iterations(conf2)
      print user, "SCRAM-SHA-512", iters2
    }
  }
' <<< "${RAW_OUT}"