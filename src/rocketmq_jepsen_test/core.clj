(ns rocketmq-jepsen-test.core
  (:require [clojure.tools.logging :refer :all]
            [clojure.string :as cstr]
            [jepsen [cli :as cli]
             [control :as c]
             [db :as db]
             [tests :as tests]
             [checker :as checker]
             [client :as client]
             [generator :as gen]
             [nemesis :as nemesis]]
            [jepsen.checker.timeline :as timeline]
            [jepsen.control.util :as cu]
            [jepsen.os :as os]))

(defonce rocketmq-path "/root/rocketmq-jepsen/rocketmq-4.5.2")
(defonce rocketmq-conf-path "/root/rocketmq-jepsen/rocketmq-4.5.2/conf")
(defonce rocketmq-dledger-port 40911)
(defonce rocketmq-start "bin/mqbroker")
(defonce rocketmq-stop "bin/mqshutdown")
(defonce rocketmq-store-path "/tmp/rmqstore")
(defonce rocketmq-log-path "/root/logs/rocketmqlogs")

(defn peer-id [node]
  (str node))

(defn peer-str [node]
  (str (peer-id node) "-" node ":" rocketmq-dledger-port))

(defn peers
  "Constructs an initial cluster string for a test, like
  \"n0-host1:20911;n1-host2:20911,...\""
  [test]
  (->> (:nodes test)
       (map (fn [node]
              (peer-str node)))
       (cstr/join ";")))

(defn start! [test node]
  (info "Start rocketmq broker" node)
  (c/cd rocketmq-path
        (c/exec :sh
                rocketmq-start
                "-c"
                "conf/jepsen-test-broker.conf")))

(defn stop! [node]
  (info "Stop rocketmq broker" node)
  (c/cd rocketmq-path
        (c/exec :sh
                rocketmq-stop
                "broker")))

(defn db
  "RocketMQ db."
  []
  (reify db/DB
    (setup! [_ test node]
      (c/exec :rm
              :-rf
              rocketmq-log-path)
      (c/cd rocketmq-conf-path
            (c/exec* "cp dledger-broker.conf jepsen-test-broker.conf"))
      (c/cd rocketmq-conf-path
            (info (c/exec*
                    (format "echo %s >> jepsen-test-broker.conf"
                            (str "dLegerPeers=" (peers test)))))
            (info (c/exec*
                    (format "echo %s >> jepsen-test-broker.conf"
                            (str "dLegerSelfId=" (peer-id node)))))
      (start! test node)
      (Thread/sleep 20000)
      ))

    (teardown! [_ test node]
      (stop! node)
      (Thread/sleep 20000)
      (c/exec :rm
              :-rf
              dledger-store-path))))

(defn rocketmq-jepsen-test
  "Given an options map from the command line runner (e.g. :nodes, :ssh,
  :concurrency, ...), constructs a test map."
  [opts]
  (merge tests/noop-test
         opts
         {:name          "rocketmq-jepsen-test"
          :os            os/noop
          :db            (db)}))

(defn -main
  "Handles command line arguments. Can either run a test, or a web server for
  browsing results."
  [& args]
  (cli/run! (merge (cli/single-test-cmd {:test-fn rocketmq-jepsen-test})
                   (cli/serve-cmd))
            args))
