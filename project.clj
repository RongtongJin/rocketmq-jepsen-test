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

(defproject rocketmq-jepsen-test "0.1.0-SNAPSHOT"
  :description "A jepsen test for rocketmq on dledger"
  :url "https://github.com/RongtongJin/rocketmq-jepsen-test"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :main rocketmq-jepsen-test.core
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [jepsen "0.1.11" :exclusions [knossos]]
                 [knossos "0.3.5" :exclusions [org.slf4j/slf4j-log4j12]]
                 [org.apache.rocketmq/rocketmq-jepsen-client "1.0-SNAPSHOT"]])
