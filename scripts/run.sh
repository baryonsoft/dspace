#!/bin/bash
if [ -z "$1" ];then
    echo "Usage: $0 [port]"
    exit 1
fi
cd `dirname $0`
sed "s/8080/$1/g" < ../conf/server.xml > /tmp/server.xml \
    && ./catalina.sh run -config /tmp/server.xml