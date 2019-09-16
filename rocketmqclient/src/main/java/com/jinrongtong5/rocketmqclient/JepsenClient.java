package com.jinrongtong5.rocketmqclient;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;

public class JepsenClient {
    private static Logger log = LoggerFactory.getLogger(JepsenClient.class);
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
        String instanceName = UUID.randomUUID().toString().substring(24);
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
            log.info("Client startup.");
            init();
            producer.start();
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        log.info("Client shutdown.");
        producer.shutdown();
        consumer.shutdown();
    }

    public int enqueue(String value) {
        Message message = new Message(topic, value.getBytes());
        SendResult sendResult;
        try {
            sendResult = producer.send(message);
        } catch (RemotingException e) {
            if (e instanceof RemotingConnectException || e instanceof RemotingSendRequestException) {
                log.warn("RemotingException error", e);
                return FAIL_CODE;
            } else {
                log.warn("RemotingException info", e);
                return INFO_CODE;
            }
        } catch (IllegalStateException e) {
            log.warn("IllegalStateException error", e);
            return FAIL_CODE;
        } catch (MQClientException e) {
            log.warn("MQClientException error", e);
            return FAIL_CODE;
        } catch (InterruptedException e) {
            log.warn("InterruptedException error", e);
            return FAIL_CODE;
        } catch (MQBrokerException e) {
            log.warn("MQBrokerException error", e);
            return FAIL_CODE;
        } catch (Exception e) {
            log.warn("Exception info", e);
            return INFO_CODE;
        }
        log.info("Send OK -- value {} = msgId:{}, messageQueue:{}, queueOffset:{}, offsetMsgId:{}.", value, sendResult.getMsgId(), sendResult.getMessageQueue(), sendResult.getQueueOffset(), sendResult.getOffsetMsgId());
        return OK_CODE;
    }

    public String dequeue() {
        List<MessageExt> messages = consumer.poll();
        if (!messages.isEmpty()) {
            consumer.commitSync();
            log.info("Receive OK -- message = {}", messages.get(0));
            return new String(messages.get(0).getBody());
        } else {
            return null;
        }
    }
}
