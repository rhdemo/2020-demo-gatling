#!/usr/bin/env bash

set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

$DIR/build.sh

docker run --rm=true \
       --env JAVA_OPTS \
       --env SIMULATION \
       quay.io/redhatdemo/2020-load-test
