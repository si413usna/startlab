#!/usr/bin/env bash

# This script compiles and runs a Java class using maven
# usage: run.sh [-c ClassName] [args...]
# The ClassName defaults to Compiler

set -euo pipefail

topdir=$(dirname "$(realpath "$0")")

if [[ $# -ge 2 && $1 = '-c' ]]; then
  class=$2
  shift 2
else
  class=ParseTreeGen
fi

mvn -f"$topdir" clean compile exec:java \
  -Dorg.slf4j.simpleLogger.defaultLogLevel=warn \
  -Dexec.mainClass="si413.$class" \
  -Dexec.args="$*"
