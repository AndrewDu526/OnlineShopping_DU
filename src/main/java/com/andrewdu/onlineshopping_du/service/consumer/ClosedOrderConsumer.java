package com.andrewdu.onlineshopping_du.service.consumer;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingOrderDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

import javax.annotation.Resource;


@Component
@Slf4j
@RocketMQMessageListener(topic = "order-topic", consumerGroup = "consumerGroup", selectorExpression = "closed-order-tag")
public class ClosedOrderConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Resource
    private OnlineShoppingOrderDao onlineShoppingOrderDao;

    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @Override
    public void onMessage(MessageExt messageExt) {
        String orderNo = new String(messageExt.getBody()); //order number

        //search orderNo in redis, DAO mysql or Java model?
        OnlineShoppingOrder order = onlineShoppingOrderDao.queryOrderByOrderNum(orderNo);// get Java model of order
        Integer orderState = order.getOrderStatus(); // 0 invalid 1 pending payment 2 success

        OnlineShoppingCommodity commodity = onlineShoppingCommodityDao.ListCommodityByCommodityId(order.getCommodityId());

        if(orderState == 1){ // expired unpaid order: locktock +1, update state 1->0
            order.setOrderStatus(0);// Java model order update state
            onlineShoppingOrderDao.updateOrder(order);// Mysql order update

            commodity.setLockStock(commodity.getLockStock() + 1);// Java model commodity update state
            onlineShoppingCommodityDao.updateCommodity(commodity);// Mysql commodity update
        }//no synchronized
        //no management of state=0, state=2
        log.info("Received message: " + orderNo);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        consumer.setMaxReconsumeTimes(2);  // 设置最大重试次数
        consumer.setConsumeTimeout(5000); // 设置消费超时时间为5秒
        consumer.setConsumeThreadMin(2);
        consumer.setConsumeThreadMax(2);
    }
}
