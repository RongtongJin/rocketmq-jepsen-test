package com.jinrongtong5.rocketmqclient;

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;

public class JepsenClient {
    private static final String topic = "jepsen_test3";
    private static final String producerGroupName = "consumer_group";
    private static final String consumerGroupName = "producer_group";
    private static final int FAIL_CODE = -1;
    private static final int OK_CODE = 0;
    private static final int INFO_CODE = 1;
    private DefaultMQProducer producer = new DefaultMQProducer(producerGroupName);
    private DefaultLitePullConsumer consumer = new DefaultLitePullConsumer(consumerGroupName);

    public void init() throws Exception {
        producer.setNamesrvAddr("localhost:9876");
        consumer.setNamesrvAddr("localhost:9876");
//        producer.setNamesrvAddr("172.16.2.120:9876");
//        consumer.setNamesrvAddr("172.16.2.120:9876");
        consumer.setAutoCommit(false);
        consumer.setPullBatchSize(1);
        consumer.subscribe(topic, "*");
    }

    public void startup() {
        try {
            init();
            producer.start();
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        producer.shutdown();
        consumer.shutdown();
    }

    public int enqueue(String value) {
        Message message = new Message(topic, value.getBytes());
        try {
            producer.send(message);
        } catch (RemotingException e) {
            e.printStackTrace();
            if (e instanceof RemotingConnectException || e instanceof RemotingSendRequestException) {
                return FAIL_CODE;
            } else {
                return INFO_CODE;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return FAIL_CODE;
        } catch (MQClientException e) {
            e.printStackTrace();
            return FAIL_CODE;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return FAIL_CODE;
        } catch (MQBrokerException e) {
            e.printStackTrace();
            return FAIL_CODE;
        } catch (Exception e) {
            e.printStackTrace();
            return INFO_CODE;
        }
        return OK_CODE;
    }

    public String dequeue() {
        List<MessageExt> messages = consumer.poll();
        if (!messages.isEmpty()) {
            consumer.commitSync();
            return new String(messages.get(0).getBody());
        } else {
            return null;
        }
    }
}
