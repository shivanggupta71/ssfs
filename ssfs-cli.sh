#!/bin/bash

if [ $# -lt 1 ]; then
  echo "Usage: ./sfss-cli.sh <command> [filename]"
  exit 1
fi

CMD="com.shivang.ssfsOauth.Main"
ARGS="$@"

mvn exec:java -q -Dexec.mainClass="$CMD" -Dexec.args="$ARGS"
