# RocketMQ-jepsen-test

A [jepsen](https://github.com/jepsen-io/jepsen) test for rocketmq on dledger

## Usage

## What is being tested?

[Apache RocketMQ](https://github.com/apache/rocketmq) is a distributed messaging and streaming platform with low latency, high performance and reliability, trillion-level capacity and flexible scalability. Since RocketMQ 4.5.0, RocketMQ supports the DLedger deployment, which is highly available, high durable and strong consistent. The test run concurrent operations from different nodes in a RocketMQ cluster and checks that the operations preserve the consistency properties defined in the test. During the test, various nemesis can be added to interfere with the operations.

Currently, verifiable model is  **total-queue**. Given a set of concurrent enqueue/dequeue to RocketMQ on dledger, verifies that every successful enqueue has a successful dequeue (what goes in must come out), queues only obey this property if the history includes draining them completely.

## Usage  

1. Prepare **one** control node and **five** db nodes and ensure that the control node can use SSH to  log into a bunch of db nodes. 
2. Install clojure, jepsen and [clojure-control](https://github.com/killme2008/clojure-control) on the control node.
3. Edit *nodes* , *control.clj* and *src/rocketmq_jepsen_test/core.clj* files to set IP address, user name and path. Those values are hardcoded in the program by now.
4. Deploy the rocketmq broker with clojure-control on the control node:
```
sh build.sh
control run rocketmq deploy
``` 
5. Run the test
```
./run_test.sh
```
### Options

See `lein run test --help` for  options. 

**nemesis**

`--nemsis NAME`, what nemesis should we run? The default value is partition-random-halves. You can also run the following nemesis:

- partition-random-node: isolates a single node from the rest of the network.

![enter image description here](http://assets.processon.com/chart_image/5d05fd1ce4b00d2a1ac788c7.png)

- partition-random-halves: cuts the network into randomly chosen halves.
 
![enter image description here](http://assets.processon.com/chart_image/5d05fb65e4b0cbb88a5f1815.png)

- kill-random-processes: kill random processes and restart them.

![enter image description here](http://assets.processon.com/chart_image/5d0c4523e4b0d4ba353ee2dd.png)

- crash-random-nodes: crash random nodes and restart them (kill processes and drop caches).

![enter image description here](http://assets.processon.com/chart_image/5d05feafe4b08ceab31d121a.png)

- hammer-time: pause random nodes with SIGSTOP/SIGCONT.

![enter image description here](http://assets.processon.com/chart_image/5d06012de4b091a8f244ba50.png)

- bridge: a grudge which cuts the network in half, but preserves a node in the middle which has uninterrupted bidirectional connectivity to both components.

![enter image description here](http://assets.processon.com/chart_image/5d06033de4b0d4295989d335.png)

- partition-majorities-ring: every node can see a majority, but no node sees the _same_ majority as any other. Randomly orders nodes into a ring.

![enter image description here](http://assets.processon.com/chart_image/5d0604d3e4b0591fc0e34259.png)

**Other options:**

`--rate HZ`, approximate number of requests per second, per thread, the default value is 10.

`--time-limit TIME`, test time limit, the default value is 60.

`--interval TIME`, nemesis interval, the default value is 15. 

`--test-count TIMES`, times to run test, the default value is 1. 

## Result

**RocketMQ on dledger has passed all the tests above.**

When you finish the test，you can excute `lein run serve`  and watch the report at http://ip_address:8080.