#!/bin/bash
pushd `dirname $0` > /dev/null
java -jar nermis.jar
popd > /dev/null
