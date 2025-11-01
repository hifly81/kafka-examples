#!/usr/bin/env bash
set -euo pipefail

# kafka-acl-dump.sh
# List Kafka ACLs via Admin API (kafka-acls.sh --bootstrap-server) over SSL/TLS,
# using a truststore (JKS or PKCS12) and optional client keystore (mTLS).
# Outputs either raw tool output or CSV parsed with a POSIX-compatible awk.
#
# Requirements:
# - bash
# - awk (POSIX/BSD or GNU) — parser is POSIX compatible
# - kafka-acls.sh available in PATH or via --kafka-bin-dir
#
# CSV columns:
# resourceType,resourceName,patternType,principal,host,operation,permissionType

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
- If --command-config is provided, TLS flags here are ignored in favor of that file.
- To avoid secrets on the process list, prefer --command-config over command-line passwords.
- CSV columns: resourceType,resourceName,patternType,principal,host,operation,permissionType
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

# Resolve kafka-acls.sh
KAFKA_ACLS="kafka-acls.sh"
if [[ -n "${KAFKA_BIN_DIR}" ]]; then
  KAFKA_ACLS="${KAFKA_BIN_DIR%/}/kafka-acls.sh"
fi
if ! command -v "${KAFKA_ACLS}" >/dev/null 2>&1; then
  echo "ERROR: ${KAFKA_ACLS} not found in PATH" >&2
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

# Run the list command (allow non-zero exit without aborting the script)
set +e
RAW_OUT="$("${KAFKA_ACLS}" --bootstrap-server "${BOOTSTRAP}" --command-config "${CLIENT_PROPS}" --list 2>&1)"
KAFKA_RC=$?
set -e

# If the tool failed and produced no output, surface the error
if [[ $KAFKA_RC -ne 0 && -z "${RAW_OUT}" ]]; then
  echo "ERROR: kafka-acls.sh failed (rc=${KAFKA_RC})" >&2
  exit $KAFKA_RC
fi

# If empty output, report and exit
if [[ -z "${RAW_OUT}" ]]; then
  echo "No ACLs returned (or insufficient authorization)."
  exit 0
fi

# Plain passthrough
if [[ "${OUTPUT}" == "plain" ]]; then
  printf "%s\n" "${RAW_OUT}"
  exit 0
fi

# POSIX-compatible CSV parsing of kafka-acls.sh --list output.
# Handles lines like:
#   Current ACLs for resource `ResourcePattern(resourceType=TOPIC, name=test, patternType=LITERAL)`:
#       (principal=User:alice, host=*, operation=WRITE, permissionType=ALLOW)
#       (principal=User:CN=Alice,OU=Eng,O=Acme, host=*, operation=READ, permissionType=ALLOW)
#
# Produces:
#   resourceType,resourceName,patternType,principal,host,operation,permissionType
awk -v OFS=',' '
  function trim(s) { gsub(/^[[:space:]]+|[[:space:]]+$/, "", s); return s }

  BEGIN {
    print "resourceType","resourceName","patternType","principal","host","operation","permissionType"
    rt=""; rn=""; pt=""
  }

  # Header describing the resource whose ACLs are listed
  $0 ~ /^Current ACLs for resource/ {
    rt=""; rn=""; pt=""
    # Extract "ResourcePattern(...)" without GNU capture arrays
    if (match($0, /ResourcePattern\([^)]*\)/)) {
      rp = substr($0, RSTART, RLENGTH)
      sub(/^ResourcePattern\(/, "", rp)
      sub(/\)$/, "", rp)
      # rp like: resourceType=TOPIC, name=myTopic, patternType=LITERAL
      n = split(rp, arr, /, */)
      for (i=1; i<=n; i++) {
        item = trim(arr[i])
        # split only on first "="
        eq = index(item, "=")
        if (eq > 0) {
          key = substr(item, 1, eq-1)
          val = substr(item, eq+1)
          if (key=="resourceType" || key=="type") rt=val
          else if (key=="name") rn=val
          else if (key=="patternType" || key=="pattern") pt=val
        }
      }
    }
    next
  }

  # Each ACL entry line (principal may contain commas; parse by token boundaries)
  $0 ~ /^[[:space:]]*\(principal=/ {
    line=$0
    gsub(/[()]/, "", line)

    pr=""; h=""; op=""; perm=""

    # principal=... , host=
    pidx = index(line, "principal=")
    if (pidx > 0) {
      start = pidx + length("principal=")
      hsep = index(line, ", host=")
      if (hsep > 0) pr = substr(line, start, hsep - start)
      else pr = substr(line, start)
    }

    # host=... , operation=
    hidx = index(line, "host=")
    if (hidx > 0) {
      start = hidx + length("host=")
      osep = index(line, ", operation=")
      if (osep > 0) h = substr(line, start, osep - start)
      else h = substr(line, start)
    }

    # operation=... , permissionType=
    oidx = index(line, "operation=")
    if (oidx > 0) {
      start = oidx + length("operation=")
      psep = index(line, ", permissionType=")
      if (psep > 0) op = substr(line, start, psep - start)
      else op = substr(line, start)
    }

    # permissionType=... (to end)
    pmidx = index(line, "permissionType=")
    if (pmidx > 0) {
      start = pmidx + length("permissionType=")
      perm = substr(line, start)
    }

    print rt, rn, pt, trim(pr), trim(h), trim(op), trim(perm)
  }
' <<< "${RAW_OUT}"