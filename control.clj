(defcluster :rocketmq
            :clients [{:host "172.16.2.121" :user "root"}
                      {:host "172.16.2.122" :user "root"}
                      {:host "172.16.2.123" :user "root"}
                      {:host "172.16.2.124" :user "root"}
                      {:host "172.16.2.127" :user "root"}])

(deftask :date "echo date on cluster" []
         (ssh "date"))

(deftask :build []
         (local (run "git submodule init;git submodule update"))
         (local
           (run
             (cd "rocketmq"
                 (run "git chekout -b jepsen_test origin/jepsen_test")
                 (run "mvn -Prelease-all -DskipTests clean install -U")
                 )))

         (local
           (run
             (cd "rocketmqclient"
                 (run "mvn clean install -DskipTests")
                 )))


         (local (run "rm rocketmq-jepsen.tar.gz;tar zcvf rocketmq-jepsen.tar.gz rocketmq/distribution/target/rocketmq-4.6.0-SNAPSHOT.tar.gz dledger-broker.conf brokershutdown.sh brokerstartup.sh")))


(deftask :deploy []
         (scp "rocketmq-jepsen.tar.gz" "/root/")
         (ssh
           (run
             (cd "/root"
                 (run "rm -rf rocketmq-jepsen/;mkdir rocketmq-jepsen")
                 (run "tar zxvf rocketmq-jepsen.tar.gz -C rocketmq-jepsen"))
             (cd "/root/rocketmq-jepsen"
                 (run "tar zxvf rocketmq/distribution/target/rocketmq-4.6.0-SNAPSHOT.tar.gz")
                 (run "mv dledger-broker.conf rocketmq-4.6.0-SNAPSHOT/conf/dledger-broker.conf")
                 (run "chmod a+x brokershutdown.sh;mv brokershutdown.sh rocketmq-4.6.0-SNAPSHOT/bin/brokershutdown.sh")
                 (run "chmod a+x brokerstartup.sh;mv brokerstartup.sh rocketmq-4.6.0-SNAPSHOT/bin/brokerstartup.sh"))
             )))