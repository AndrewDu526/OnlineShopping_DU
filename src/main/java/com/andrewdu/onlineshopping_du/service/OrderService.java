package com.andrewdu.onlineshopping_du.service;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingOrderDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingOrder;
import com.andrewdu.onlineshopping_du.service.mq.RocketMQService;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;


@Service
@Slf4j
public class OrderService {
    @Resource
    OnlineShoppingOrderDao onlineShoppingOrderDao;

    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @Resource
    private RedisService redisService;
    @Resource
    private RocketMQService rocketMQService;

    public OnlineShoppingOrder placeOrderOriginal(long commodityId, long userId) {
        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.ListCommodityByCommodityId(commodityId);
        int availableStock = onlineShoppingCommodity.getAvailableStock();
        int lockStock = onlineShoppingCommodity.getLockStock();
        if (availableStock > 0) {
            availableStock--;
            lockStock++;
            onlineShoppingCommodity.setAvailableStock(availableStock);
            onlineShoppingCommodity.setLockStock(lockStock);
            onlineShoppingCommodityDao.updateCommodity(onlineShoppingCommodity);
            OnlineShoppingOrder order = createOrder(commodityId, userId, onlineShoppingCommodity.getPrice());
            onlineShoppingOrderDao.insertOrder(order);
            log.info("Place order successfully, current availableStock:" + availableStock);

            rocketMQService.sendDelayedMessage("order-topic", "closed-order-tag", order.getOrderNo(), 16);

            return order;
        } else {
            log.warn("commodity out of stock, commodityId:" + onlineShoppingCommodity.getCommodityId());
            return null;
        }
    }

    public OnlineShoppingOrder placeOrderRedis(long commodityId, long userId) {
        String redisKey = "online_shopping:online_shopping_commodity:stock:" + commodityId;
        long result = redisService.deductStockWithCommodityId(redisKey);
        if (result >= 0) {
            OnlineShoppingOrder order = placeOrderOriginal(commodityId, userId);
            log.info("Place order successfully");
            return order;
        } else {
            log.warn("commodity out of stock, commodityId:" + commodityId);
            return null;
        }
    }

    private  OnlineShoppingOrder createOrder(long commodityId, long userId, long orderAmount) {
        OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                .commodityId(commodityId)
                .orderNo(UUID.randomUUID().toString())
                .orderStatus(1)
                // 0: invalid order
                // 1. pending payment
                // 2. finish payment
                // 99. overtime order
                .orderAmount(orderAmount)
                .createTime(new Date())
                .userId(userId)
                .build();
        return order;
    }

    public OnlineShoppingOrder queryOrderByOrderNum(String orderNum) {
        OnlineShoppingOrder onlineShoppingOrder = onlineShoppingOrderDao.queryOrderByOrderNum(orderNum);
        return onlineShoppingOrder;
    }

    public int payOrder(String orderNum) {
        OnlineShoppingOrder onlineShoppingOrder = queryOrderByOrderNum(orderNum);
        onlineShoppingOrder.setOrderStatus(2);
        onlineShoppingOrder.setPayTime(new Date());
        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.ListCommodityByCommodityId(onlineShoppingOrder.getCommodityId());
        onlineShoppingCommodity.setLockStock(onlineShoppingCommodity.getLockStock() - 1);
        onlineShoppingCommodityDao.updateCommodity(onlineShoppingCommodity);
        return onlineShoppingOrderDao.updateOrder(onlineShoppingOrder);
    }
}