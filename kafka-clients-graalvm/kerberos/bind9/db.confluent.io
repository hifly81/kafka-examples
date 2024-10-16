$TTL 86400
@   IN  SOA dns.confluent.io. root.confluent.io. (
        1   ; Serial
        604800  ; Refresh
        86400   ; Retry
        2419200 ; Expire
        604800) ; Negative Cache TTL

; Name servers
    IN  NS  dns.confluent.io.

; KDC server
kdc  IN  A   192.168.0.3