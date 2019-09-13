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
            [knossos.core          :as knossos]
            [knossos.op            :as op]
            [knossos.model    :as model]
            [jepsen.checker.timeline :as timeline]
            [jepsen.control.util :as cu]
            [jepsen.os :as os])
  (:import [com.jinrongtong5.rocketmqclient JepsenClient]))


(defonce rocketmq-path "/root/rocketmq-jepsen/rocketmq-4.6.0-SNAPSHOT")
(defonce rocketmq-conf-path "/root/rocketmq-jepsen/rocketmq-4.6.0-SNAPSHOT/conf")
(defonce rocketmq-dledger-port 40911)
(defonce rocketmq-start "bin/brokerstartup.sh")
(defonce rocketmq-stop "bin/brokershutdown.sh")
(defonce rocketmq-store-path "/tmp/rmqstore")
(defonce rocketmq-log-path "/root/logs/rocketmqlogs")

(def dledger-self-id (hash-map "172.16.2.121" "n0" "172.16.2.122" "n1" "172.16.2.123" "n2" "172.16.2.124" "n3" "172.16.2.127" "n4"))

(defn peer-id [node]
  (get dledger-self-id (str node)))

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
                rocketmq-stop)))

(defn db
  "RocketMQ db."
  []
  (reify db/DB
    (setup! [_ test node]
      (c/exec :rm
              :-rf
              rocketmq-log-path)
      (c/exec :rm
              :-rf
              rocketmq-store-path)
      (c/cd rocketmq-conf-path
            (c/exec* "cp dledger-broker.conf jepsen-test-broker.conf"))
      (c/cd rocketmq-conf-path
            (info (c/exec*
                    (format "echo \"%s\" >> jepsen-test-broker.conf"
                            (str "dLegerPeers=" (peers test)))))
            (info (c/exec*
                    (format "echo \"%s\" >> jepsen-test-broker.conf"
                            (str "dLegerSelfId=" (peer-id node))))))
      (start! test node)
      (Thread/sleep 20000))

    (teardown! [_ test node]
      (stop! node)
      (Thread/sleep 20000)
      (c/exec :rm
              :-rf
              rocketmq-store-path))))

(defn- create-client [test]
  (doto (JepsenClient.) (.startup)))

(defn- shutdown-client [client]
  (-> client
      :conn
      (.shutdown)))

(defn- enqueue
  "enqueue element to rocketmq"
  [client value]
  (-> client
      :conn
      (.enqueue (pr-str value))))

(defn- dequeue
  "dequeue element from rocketmq"
  [client op]
  (let [res, (-> client :conn (.dequeue))]
    (if (nil? res)
      (assoc op :type :fail :error :empty)
      (assoc op :type :ok :value (read-string res)))))

(defrecord Client [conn]
  client/Client
  (open! [this test node]
    (-> this
        (assoc :node node)
        (assoc :conn (create-client test))))

  (setup! [this test])

  (invoke! [this test op]
    (try
      (case (:f op)
        :enqueue (let [code, (enqueue this (:value op))]
                   (cond
                     (= code -1) (assoc op :type :fail)
                     (= code 0) (assoc op :type :ok)
                     (= code 1) (assoc op :type :info)
                     :else (assoc op :type :fail :error (str "error code: " code))))

        :dequeue (dequeue this op)

        :drain (loop [values []]
                 (let [res (dequeue this op)]
                   (if (= (:type res) :ok)
                     (recur (conj values (:value res)))
                     (assoc op :type :ok, :value values)))))

      (catch Exception e
        (assoc op :type :info :error e))))

  (teardown! [this test])

  (close! [this test]
    (shutdown-client this)
    ))

(defn rocketmq-jepsen-test
  "Given an options map from the command line runner (e.g. :nodes, :ssh,
  :concurrency, ...), constructs a test map."
  [opts]
  (merge tests/noop-test
         opts
         {:name   "rocketmq-jepsen-test"
          :os     os/noop
          :db     (db)
          :client (Client. nil)
          :model      (model/unordered-queue)
          :checker    (checker/compose
                       {
                         :total-queue (checker/total-queue)})
          :generator  (gen/phases
                       (->> (gen/queue)
                            (gen/delay 1)
                            (gen/nemesis nil)
                            (gen/time-limit 60))
                       (gen/sleep 15)
                       (gen/clients
                        (gen/each
                         (gen/once {:type :invoke, :f :drain}))))
                            }))

(defn -main
  "Handles command line arguments. Can either run a test, or a web server for
  browsing results."
  [& args]
  (cli/run! (merge (cli/single-test-cmd {:test-fn rocketmq-jepsen-test})
                   (cli/serve-cmd))
            args))
