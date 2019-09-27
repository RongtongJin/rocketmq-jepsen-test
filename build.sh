#!/bin/sh

git submodule init
git submodule update

cd openmessaging-storage-dledger
git checkout -b fix_preferredLeader origin/fix_preferredLeader
git checkout fix_preferredLeader
mvn clean install -DskipTests
cd ..

cd rocketmq
git checkout -b jepsen_test origin/jepsen_test
git checkout jepsen_test
mvn -Prelease-all -DskipTests clean install -U
cd ..

cd rocketmqclient
mvn clean install -DskipTests
cd ..

rm -rf rocketmq-jepsen.tar.gz
tar zcvf rocketmq-jepsen.tar.gz rocketmq/distribution/target/rocketmq-4.6.0-SNAPSHOT.tar.gz dledger-broker.conf brokershutdown.sh brokerstartup.sh stop_dropcaches.sh
