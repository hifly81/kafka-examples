#!/usr/bin/env bash

set -x
/usr/bin/trogdor agent -c /tmp/trogdor/agent/trogdor-agent.conf -n node1 >/home/appuser/trogdor-agent.log 2>&1 &