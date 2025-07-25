package com.andrewdu.onlineshopping_du.controller;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingOrder;
import com.andrewdu.onlineshopping_du.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class OrderController {
    @Resource
    OrderService orderService;

    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @RequestMapping("commodity/buy/{userId}/{commodityId}")
    public String buyCommodity(@PathVariable("userId") long userId,
                               @PathVariable("commodityId") long commodityId,
                               Map<String, Object> resultMap) {
        OnlineShoppingOrder order =
//                orderService.placeOrderOriginal(commodityId, userId);
//                orderService.placeOrderOneSQL(commodityId, userId);
                  orderService.placeOrderRedis(commodityId, userId);
//                orderService.placeOrderWithDistributedLock(commodityId, userId);
        if (order == null) {
            resultMap.put("resultInfo", "Order create failed, check log for detail");
            resultMap.put("orderNo", "");
        } else {
            resultMap.put("resultInfo", "Order created successfully");
            resultMap.put("orderNo", order.getOrderNo());
        }
        return "order_result";
    }

    @RequestMapping("commodity/orderQuery/{orderNum}")
    public String orderQuery(@PathVariable("orderNum") String orderNum,
                               Map<String, Object> resultMap) {
        OnlineShoppingOrder onlineShoppingOrder =
                orderService.queryOrderByOrderNum(orderNum);
        resultMap.put("order",  onlineShoppingOrder);
        OnlineShoppingCommodity onlineShoppingCommodity =
                onlineShoppingCommodityDao.ListCommodityByCommodityId(onlineShoppingOrder.getCommodityId());
        resultMap.put("commodity",  onlineShoppingCommodity);
        return "order_check";
    }

    @RequestMapping("commodity/payOrder/{orderNum}")
    public String payOrder(@PathVariable("orderNum") String orderNum,
                             Map<String, Object> resultMap) {
        orderService.payOrder(orderNum);
        return orderQuery(orderNum, resultMap);
    }
}

