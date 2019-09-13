(defproject rocketmq-jepsen-test "0.1.0-SNAPSHOT"
  :description "A jepsen test for rocketmq on dledger"
  :url "https://github.com/RongtongJin/rocketmq-jepsen-test"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :main rocketmq-jepsen-test.core
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [jepsen "0.1.15"]
                 [knossos "0.3.4-SNAPSHOT"]
                 [com.jinrongtong5/rocketmq-client "1.0-SNAPSHOT"]])
