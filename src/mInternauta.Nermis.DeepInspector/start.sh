#!/bin/bash
pushd `dirname $0` > /dev/null
java -jar DeepInspector.jar
popd > /dev/null
