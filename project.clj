(defproject rocketmq-jepsen-test "0.1.0-SNAPSHOT"
  :description "A jepsen test for rocketmq on dledger"
  :url "https://github.com/RongtongJin/rocketmq-jepsen-test"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :main rocketmq-jepsen-test.core
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [jepsen "0.1.11" :exclusions [knossos]]
                 [knossos "0.3.5" :exclusions [org.slf4j/slf4j-log4j12]]
                 [com.jinrongtong5/rocketmq-client "1.0-SNAPSHOT"]])
