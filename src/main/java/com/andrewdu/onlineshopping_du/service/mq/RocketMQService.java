package com.andrewdu.onlineshopping_du.service.mq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.apache.rocketmq.common.message.Message;

import javax.annotation.Resource;

@Service
public class RocketMQService {

    // @SpringBootApplication -> RocketMQ jar dependency -> @EnableAutoConfiguration -> RocketMQConfiguration -> @Bean RocketMQTemplate
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage(String topic, String tag, String messageBody){
        Message message = new Message(topic, tag, messageBody.getBytes());
        try{
            rocketMQTemplate.getProducer().send(message);
        }catch (MQClientException e) {
            throw new RuntimeException(e);
        } catch (RemotingException e) {
            throw new RuntimeException(e);
        } catch (MQBrokerException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendOrderedMessage(String topic, String messageBody){ // FIFO
        Message message = new Message(topic, messageBody.getBytes());
        try{
            rocketMQTemplate.getProducer().send(message, (mqs, msg, arg)->mqs.get(0), null);
        }catch (MQClientException e) {
            throw new RuntimeException(e);
        } catch (RemotingException e) {
            throw new RuntimeException(e);
        } catch (MQBrokerException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendDelayedMessage(String topic, String tag, String messageBody, int delayTimeLevel){
        Message message = new Message(topic, tag, messageBody.getBytes());
        message.setDelayTimeLevel(delayTimeLevel);
        try {
            rocketMQTemplate.getProducer().send(message);
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        } catch (RemotingException e) {
            throw new RuntimeException(e);
        } catch (MQBrokerException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
