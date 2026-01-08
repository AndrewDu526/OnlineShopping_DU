package com.andrewdu.onlineshopping_du.service.mq;

import com.alibaba.fastjson.JSON;
import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingOrderDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@RocketMQMessageListener(topic = "createOrder", consumerGroup = "createOrderGroup")
public class CreateOrderListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Resource
    private RocketMQService rocketMQService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        // insert into Commodity Table
        // insert into Order Table
        OnlineShoppingOrder order = JSON.parseObject(message, OnlineShoppingOrder.class);
        Long commodityId = order.getCommodityId();
        Long userId = order.getUserId();
        int res = onlineShoppingCommodityDao.deductStockWithCommodityId(commodityId);
        if (res > 0) {
            onlineShoppingOrderDao.insertOrder(order);
            rocketMQService.sendDelayMessage("paymentCheck", JSON.toJSONString(order), 4);
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setMaxReconsumeTimes(2);  // 设置最大重试次数
        consumer.setConsumeTimeout(5); // 设置消费超时时间为1min
        consumer.setConsumeThreadMin(2);
        consumer.setConsumeThreadMax(2);
    }
}

