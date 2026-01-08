package com.andrewdu.onlineshopping_du.service.mq;

import com.alibaba.fastjson.JSON;
import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingOrderDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingOrder;
import com.andrewdu.onlineshopping_du.service.RedisService;
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
@RocketMQMessageListener(topic = "paymentCheck", consumerGroup = "paymentCheckGroup")
public class PaymentCheckListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    @Resource
    OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    RedisService redisService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        OnlineShoppingOrder orderFromMsg = JSON.parseObject(message, OnlineShoppingOrder.class);
        OnlineShoppingOrder order = onlineShoppingOrderDao.queryOrderByOrderNum(orderFromMsg.getOrderNo());
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        Integer orderStatus = order.getOrderStatus();
        if (orderStatus == 2) {
            log.info("Order has finished payment, skip order check");
        } else {
            // if not, revert Commodity stock, update Order status to 99
            log.info("Didn't pay the order on time, order number：{}", order.getOrderNo());
            order.setOrderStatus(99);
            onlineShoppingOrderDao.updateOrder(order);
            onlineShoppingCommodityDao.revertStockWithCommodityId(order.getCommodityId());
            String redisKey = "online_shopping:online_shopping_commodity:stock:" + order.getCommodityId();
            Long currentStockCount = redisService.revertStock(redisKey);
            redisService.removeFromDenyList(order.getUserId().toString(), order.getCommodityId().toString());
            log.info("Redis revert commodityId {}, current Available Count：{}",
                    order.getCommodityId(),
                    currentStockCount);
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setMaxReconsumeTimes(2);  // 设置最大重试次数
        consumer.setConsumeTimeout(1); // 设置消费超时时间为1min
        consumer.setConsumeThreadMin(2);
        consumer.setConsumeThreadMax(2);
    }
}
