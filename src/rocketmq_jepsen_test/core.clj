(ns rocketmq-jepsen-test.core
  (:require [jepsen.cli :as cli]
            [jepsen.tests :as tests]))



(defn rocketmq-jepsen-test
  "Given an options map from the command line runner (e.g. :nodes, :ssh,
  :concurrency, ...), constructs a test map."
  [opts]
  (merge tests/noop-test
         opts))

(defn -main
  "Handles command line arguments. Can either run a test, or a web server for
  browsing results."
  [& args]
  (cli/run! (merge (cli/single-test-cmd {:test-fn rocketmq-jepsen-test})
                   (cli/serve-cmd))
            args))
