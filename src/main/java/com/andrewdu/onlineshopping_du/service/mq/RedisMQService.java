package com.andrewdu.onlineshopping_du.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisMQService {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    public void sendMsg(String queueName, Object message){
        redisTemplate.opsForList().leftPush(queueName, message);
    }

    public Object receiveMsg(String queueName){
        return redisTemplate.opsForList().rightPop(queueName);
    }

    public Object receiveMsgBlocking(String queueName, long timeoutSeconds){
        return redisTemplate.opsForList().rightPop(queueName, timeoutSeconds, TimeUnit.SECONDS);
    }

    public void sendMsgDelayed(String queueName, Object message, long delaySeconds){
        long executeTime = System.currentTimeMillis()+delaySeconds*1000;
        redisTemplate.opsForZSet().add(queueName, message, executeTime);

        log.info("发送延时消息: {}, 延时: {}秒", message, delaySeconds);
    }

    public void receiveMsgDelayed(){

    }
}
