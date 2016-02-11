#!/bin/bash
pushd `dirname $0` > /dev/null
ARGS=--service
java -jar nermis.jar "$ARGS"
popd > /dev/null
