tr '\n' ' ' < insert.sql | \
sed 's/;/;\'$'\n''/g' | \
while read stmt; do
    echo '{"ksql":"'$stmt'", "streamsProperties": {}}' | \
        curl -s -X "POST" "http://ksqldb-server:8088/ksql" \
             -H "Content-Type: application/vnd.ksql.v1+json; charset=utf-8" \
             -d @- | \
        jq
done
