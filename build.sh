#!/bin/sh

git submodule init
git submodule update

cd rocketmq
git checkout -b develop origin/develop
mvn -Prelease-all -DskipTests clean install -U
cd ..

cd rocketmqclient
mvn clean install -DskipTests
cd ..

rm -rf rocketmq-jepsen.tar.gz
tar zcvf rocketmq-jepsen.tar.gz rocketmq/distribution/target/rocketmq-4.6.0-SNAPSHOT.tar.gz \
 scripts/dledger-broker.conf scripts/brokershutdown.sh scripts/brokerstartup.sh scripts/stop_dropcaches.sh
