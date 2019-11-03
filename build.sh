#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

cd rocketmq
git branch develop origin/develop
git checkout develop
mvn -Prelease-all -DskipTests clean install -U
cd ..

cd rocketmq-jepsen-client
mvn clean install -DskipTests
cd ..

rm -rf rocketmq-jepsen.tar.gz
tar zcvf rocketmq-jepsen.tar.gz rocketmq/distribution/target/rocketmq-4.6.0-SNAPSHOT.tar.gz \
 scripts/dledger-broker.conf scripts/brokershutdown.sh scripts/brokerstartup.sh scripts/stop_dropcaches.sh
