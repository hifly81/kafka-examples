#!/bin/bash

ACL_FILE=/tmp/acl-config.yaml

# Parse simple key-value pairs from YAML
NAME=$(grep 'name:' $ACL_FILE | sed 's/.*: //')
ACTION=$(grep 'action:' $ACL_FILE | sed 's/.*: //')
ALLOW_PRINCIPAL=$(grep 'allow_principal:' $ACL_FILE | sed 's/.*: //')
TRANSACTIONAL_ID=$(grep 'transactional_id:' $ACL_FILE| sed 's/.*: //')
RESOURCE_PATTERN_TYPE=$(grep 'resource_pattern_type:' $ACL_FILE | sed 's/.*: //')

# Extract and clean operations (remove '-' and trim spaces)
OPERATIONS=()
while IFS= read -r line; do
    op=$(echo "$line" | sed -E 's/^\s*-\s*//' | xargs) # Remove '- ' and trim whitespace
    [[ -n "$op" ]] && OPERATIONS+=("$op") # Add only if non-empty
done < <(awk '/operations:/ {flag=1; next} /^[^ ]/ {flag=0} flag' $ACL_FILE)

# Extract and clean topics (remove '-' and trim spaces)
TOPICS=()
while IFS= read -r line; do
    topic=$(echo "$line" | sed -E 's/^\s*-\s*//' | xargs) # Remove '- ' and trim whitespace
    [[ -n "$topic" ]] && TOPICS+=("$topic") # Add only if non-empty
done < <(awk '/topics:/ {flag=1; next} /^[^ ]/ {flag=0} flag' $ACL_FILE)

# Display the ACL name for better logging
echo "Running ACL Setup: $NAME"

# Loop through each topic and apply ACLs
for topic in "${TOPICS[@]}"; do
    # Construct the base command
    CMD="/opt/kafka/bin/kafka-acls.sh --bootstrap-server broker:9092 --command-config /tmp/admin.properties $ACTION --allow-principal \"$ALLOW_PRINCIPAL\""

    # Add each operation correctly
    for op in "${OPERATIONS[@]}"; do
        CMD+=" --operation $op"
    done

    # Add the topic and resource pattern type
    CMD+=" --topic $topic"
    CMD+=" --resource-pattern-type $RESOURCE_PATTERN_TYPE"

    # Include transactional ID if provided
    if [[ -n "$TRANSACTIONAL_ID" ]]; then
        CMD+=" --transactional-id $TRANSACTIONAL_ID"
    fi

    # Print and execute the command
    echo "Executing: $CMD"
    eval "$CMD"
done
