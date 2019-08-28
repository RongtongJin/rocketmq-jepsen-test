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


(defn db
  "RocketMQ db."
  []
  (reify db/DB
    (setup! [_ test node]
      (start! test node)
      (Thread/sleep 20000)
      )

    (teardown! [_ test node]
      (stop! node)
      (Thread/sleep 20000)
      (c/exec :rm
              :-rf
              dledger-data-path))))

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
