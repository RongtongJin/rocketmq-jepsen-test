package com.jinrongtong5.rocketmqclient;

import java.util.List;
import java.util.UUID;
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
    private static final String topic = "jepsen_test";
    private static final String producerGroupName = "producer_group";
    private static final String consumerGroupName = "consumer_group";
    private static final int FAIL_CODE = -1;
    private static final int OK_CODE = 0;
    private static final int INFO_CODE = 1;
    private DefaultMQProducer producer = new DefaultMQProducer(producerGroupName);
    private DefaultLitePullConsumer consumer = new DefaultLitePullConsumer(consumerGroupName);

    public void init() throws Exception {
//        producer.setNamesrvAddr("localhost:9876");
//        consumer.setNamesrvAddr("localhost:9876");
        String instanceName  = UUID.randomUUID().toString().substring(24);
        producer.setInstanceName(instanceName);
        consumer.setInstanceName(instanceName);
        producer.setNamesrvAddr("172.16.2.120:9876");
        consumer.setNamesrvAddr("172.16.2.120:9876");
        consumer.setPollTimeoutMillis(500);
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
                System.out.println("RemotingException error");
                return FAIL_CODE;
            } else {
                System.out.println("RemotingException info");
                return INFO_CODE;
            }
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException error");
            e.printStackTrace();
            return FAIL_CODE;
        } catch (MQClientException e) {
            System.out.println("MQClientException error");
            e.printStackTrace();
            return FAIL_CODE;
        } catch (InterruptedException e) {
            System.out.println("InterruptedException error");
            e.printStackTrace();
            return FAIL_CODE;
        } catch (MQBrokerException e) {
            System.out.println("MQBrokerException error");
            e.printStackTrace();
            return FAIL_CODE;
        } catch (Exception e) {
            System.out.println("Exception info");
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
