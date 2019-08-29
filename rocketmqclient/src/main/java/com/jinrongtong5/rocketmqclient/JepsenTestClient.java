package com.jinrongtong5.rocketmqclient;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

public class JepsenTestClient {
    DefaultMQProducer producer = new DefaultMQProducer("producer_group");
    DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("consumer_group");

    public void init() throws Exception {
        producer.setNamesrvAddr("172.16.2.120:9876");
        consumer.setNamesrvAddr("172.16.2.120:9876");
    }

    public void startup() throws Exception {
        producer.start();
        consumer.start();
    }

    public void shutdown() throws Exception {
        producer.shutdown();
        consumer.shutdown();
    }
}
