#!/bin/sh

SCALA=$( which scala )

if [ -z ${SCALA} ]; then
    echo "You don't seem to have scala in your path. You may wanna fix that."
    exit 1
fi

CWD=$( pwd )

sbt package && \
${SCALA} -cp ${CWD}/lib_managed/scala_2.8.1/compile/*:${CWD}/target/scala_2.8.1/* com.wordnik.irc.Main $@
