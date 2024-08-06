#!/usr/bin/env bash

set -x
/kafka_2.13-3.8.0/bin/trogdor.sh coordinator -c /tmp/trogdor/coordinator/trogdor-coordinator.conf -n node0 >/tmp/trogdor/coordinator/trogdor-coordinator.log 2>&1 &
