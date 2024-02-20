#!/bin/bash

echo "Stop UDP receivers..."
echo -n "end" | nc -4u -w0 230.0.0.0 4446