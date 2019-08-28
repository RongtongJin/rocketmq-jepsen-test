(defcluster :rocketmq
            :clients [{:host "172.16.2.121" :user "root"}
                      {:host "172.16.2.122" :user "root"}
                      {:host "172.16.2.123" :user "root"}
                      {:host "172.16.2.124" :user "root"}
                      {:host "172.16.2.127" :user "root"}])

(deftask :date "echo date on cluster"  []
         (ssh "date"))

(deftask :build []
         (local (run "git submodule init;git submodule update"))
         (local
           (run
             (cd "rocketmq"
                 (run "mvn -Prelease-all -DskipTests clean install -U")
                 )))

         (local (run "rm rocketmq-jepsen.tar.gz;tar zcvf rocketmq-jepsen.tar.gz rocketmq/distribution/target/rocketmq-4.5.2.tar.gz dledger-broker.conf")))


(deftask :deploy []
         (scp "rocketmq-jepsen.tar.gz" "/root/")
         (ssh
           (run
             (cd "/root"
                 (run "rm -rf rocketmq-jepsen/;mkdir rocketmq-jepsen")
                 (run "tar zxvf rocketmq-jepsen.tar.gz -C rocketmq-jepsen"))
             (cd "/root/rocketmq-jepsen"
                 (run "tar zxvf distribution/target/rocketmq-4.5.2.tar.gz"))
             )))