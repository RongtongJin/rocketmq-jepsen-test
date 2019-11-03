; Licensed to the Apache Software Foundation (ASF) under one or more
; contributor license agreements.  See the NOTICE file distributed with
; this work for additional information regarding copyright ownership.
; The ASF licenses this file to You under the Apache License, Version 2.0
; (the "License"); you may not use this file except in compliance with
; the License.  You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(defcluster :rocketmq
            :clients [{:host "172.16.2.121" :user "root"}
                      {:host "172.16.2.122" :user "root"}
                      {:host "172.16.2.123" :user "root"}
                      {:host "172.16.2.124" :user "root"}
                      {:host "172.16.2.127" :user "root"}])

(deftask :date "echo date on cluster" []
         (ssh "date"))

(deftask :deploy []
         (scp "rocketmq-jepsen.tar.gz" "/root/")
         (ssh
           (run
             (cd "/root"
                 (run "rm -rf rocketmq-jepsen;mkdir rocketmq-jepsen")
                 (run "tar zxvf rocketmq-jepsen.tar.gz -C rocketmq-jepsen"))
             (cd "/root/rocketmq-jepsen"
                 (run "tar zxvf rocketmq/distribution/target/rocketmq-4.6.0-SNAPSHOT.tar.gz")
                 (run "mv scripts/dledger-broker.conf rocketmq-4.6.0-SNAPSHOT/conf/dledger-broker.conf")
                 (run "chmod a+x scripts/brokershutdown.sh;mv scripts/brokershutdown.sh rocketmq-4.6.0-SNAPSHOT/bin/brokershutdown.sh")
                 (run "chmod a+x scripts/brokerstartup.sh;mv scripts/brokerstartup.sh rocketmq-4.6.0-SNAPSHOT/bin/brokerstartup.sh")
                 (run "chmod a+x scripts/stop_dropcaches.sh;mv scripts/stop_dropcaches.sh rocketmq-4.6.0-SNAPSHOT/bin/stop_dropcaches.sh"))
             )))

(deftask :start []
         (ssh
           (run
             (cd "/root/rocketmq-jepsen/rocketmq-4.6.0-SNAPSHOT"
                 (run "sh bin/brokerstartup.sh -c conf/jepsen-test-broker.conf")))))

(deftask :stop []
         (ssh
           (run
             (cd "/root/rocketmq-jepsen/rocketmq-4.6.0-SNAPSHOT"
                 (run "sh bin/brokershutdown.sh")))))