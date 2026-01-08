package com.andrewdu.onlineshopping_du.service;

import com.alibaba.fastjson.JSON;
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

    // 直接更新数据库
    public OnlineShoppingOrder placeOrderOriginal(long commodityId, long userId) {
        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectCommodityById(commodityId);
        int availableStock = onlineShoppingCommodity.getAvailableStock();
        int lockStock = onlineShoppingCommodity.getLockStock();
        if (availableStock > 0 ) {
            availableStock--;
            lockStock++;
            onlineShoppingCommodity.setAvailableStock(availableStock);
            onlineShoppingCommodity.setLockStock(lockStock);
            onlineShoppingCommodityDao.updateCommodity(onlineShoppingCommodity);
            OnlineShoppingOrder order = createOrder(commodityId, userId, onlineShoppingCommodity.getPrice());
            onlineShoppingOrderDao.insertOrder(order);
            log.info("Place order successfully, current availableStock:" +  availableStock);
            return order;
        }
        else {
            log.warn("commodity out of stock, commodityId:" + onlineShoppingCommodity.getCommodityId());
            return null;
        }
    }
    public OnlineShoppingOrder placeOrderFinal(long commodityId, long userId){
        log.info("=== Starting placeOrderFinal - commodityId: {}, userId: {}", commodityId, userId);

        // 检查黑名单
        if(redisService.isInDenyList(String.valueOf(userId), String.valueOf(commodityId))){
            log.warn("User {} is in deny list for commodity {}", userId, commodityId);
            return null;
        }
        log.info("User not in deny list, proceeding...");

        // 扣减库存
        String redisKey = "online_shopping:online_shopping_commodity:stock:" + commodityId;
        log.info("Attempting to deduct stock with key: {}", redisKey);

        long result = redisService.stockDeduct(redisKey);
        log.info("Stock deduct result: {}", result);

        if (result >= 0 ) {
            log.info("Stock sufficient, creating order...");

            OnlineShoppingOrder order = createOrder(commodityId, userId, 1);

            if (order == null) {
                log.error("createOrder returned null!");
                return null;
            }

            log.info("Order created: {}", order.getOrderNo());

            try {
                rocketMQService.sendFIFOMessage("createOrder", JSON.toJSONString(order), commodityId);
                log.info("MQ message sent successfully");
            } catch (Exception e) {
                log.error("Failed to send MQ message", e);
            }

            redisService.addToDenyList(String.valueOf(userId), String.valueOf(commodityId));
            log.info("User added to deny list");

            return order;
        } else {
            log.warn("Commodity out of stock - commodityId: {}, result: {}", commodityId, result);
            return null;
        }
    }


    // 使用redis和lua保证原子性下单
    public OnlineShoppingOrder placeOrderRedis(long commodityId, long userId) {
        String redisKey = "online_shopping:online_shopping_commodity:stock:" + commodityId;
        long result = redisService.stockDeduct(redisKey);
        if (result >= 0 ) {
            // 在保证库存下，操作数据库
            OnlineShoppingOrder order = placeOrderOriginal(commodityId, userId);
            // 直接操作redis，代替原本数据库，存在redis崩溃风险和容量上限
            // OnlineShoppingOrder order = createOrder(commodityId, userId, 1);
            // onlineShoppingOrderDao.insertOrder(order);
            log.info("Place order successfully");
            return order;
        } else {
            log.warn("commodity out of stock, commodityId:" + commodityId);
            return null;
        }
    }

    // TODO: 了解数据库事务
    // 数据库SQL层面的原子性
    public OnlineShoppingOrder placeOrderOneSQL(long commodityId, long userId) {
        int result = onlineShoppingCommodityDao.deductStockWithCommodityId(commodityId);
        if (result > 0 ) {
            OnlineShoppingOrder order = createOrder(commodityId, userId, 1);
            onlineShoppingOrderDao.insertOrder(order);
            log.info("Place order successfully");
            return order;
        }
        else {
            log.warn("commodity out of stock, commodityId:" + commodityId);
            return null;
        }
    }

    // TODO: 了解分布式锁
    // 分布式锁实现原子性
    public OnlineShoppingOrder placeOrderWithDistributedLock(long commodityId, long userId) {
        String redisKey = "commodityLock:" + commodityId;
        String requestId = UUID.randomUUID().toString();
        boolean result = redisService.tryToGetDistributedLock(redisKey, requestId, 5000);
        if (result) {
            OnlineShoppingOrder order = placeOrderOriginal(commodityId, userId);
            redisService.releaseDistributedLock(redisKey, requestId);
            // OnlineShoppingOrder order = createOrder(commodityId, userId, 1);
            // onlineShoppingOrderDao.insertOrder(order);
            log.info("Place order successfully");
            return order;
        }
        else {
            log.warn("processOrderRedis failed due to not fetching Lock, please try again latter, commodityId:" + commodityId);
            return null;
        }
    }

    // 创建订单实例
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
        try{
            rocketMQService.sendMessage("creatOrder", JSON.toJSONString(order));
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return order;
    }

    // 查询订单
    public OnlineShoppingOrder queryOrderByOrderNum(String orderNum) {
        OnlineShoppingOrder onlineShoppingOrder = onlineShoppingOrderDao.queryOrderByOrderNum(orderNum);
        return onlineShoppingOrder;
    }

    // 支付订单
    public int payOrder(String orderNum) {
        OnlineShoppingOrder onlineShoppingOrder = queryOrderByOrderNum(orderNum);
        onlineShoppingOrder.setOrderStatus(2);
        onlineShoppingOrder.setPayTime(new Date());
        OnlineShoppingCommodity onlineShoppingCommodity =
                onlineShoppingCommodityDao.selectCommodityById(onlineShoppingOrder.getCommodityId());
        onlineShoppingCommodity.setLockStock(onlineShoppingCommodity.getLockStock() - 1);
        onlineShoppingCommodityDao.updateCommodity(onlineShoppingCommodity);
        return onlineShoppingOrderDao.updateOrder(onlineShoppingOrder);
    }
}