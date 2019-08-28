package com.jinrongtong5.rocketmqclient;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

public class JepsenTestClient {
    DefaultMQProducer producer = new DefaultMQProducer("producer_group");
    DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("consumer_group");

    public void startup() throws Exception{
        producer.start();
        consumer.start();

    }
}
