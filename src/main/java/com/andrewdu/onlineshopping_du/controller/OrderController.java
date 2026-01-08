package com.andrewdu.onlineshopping_du.controller;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingOrder;
import com.andrewdu.onlineshopping_du.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private OnlineShoppingCommodityDao commodityDao;

    /**
     * 下单
     * POST /orders
     */
    @PostMapping("/orders")
    public String createOrder(
            @RequestParam("userId") long userId,
            @RequestParam("commodityId") long commodityId,
            Map<String, Object> model
    ) {
        OnlineShoppingOrder order // = orderService.placeOrderRedis(commodityId, userId);
                                     =  orderService.placeOrderFinal(commodityId, userId);
        if (order == null) {
            model.put("resultInfo", "Order create failed, check log for detail");
            model.put("orderNo", "");
        } else {
            model.put("resultInfo", "Order created successfully");
            model.put("orderNo", order.getOrderNo());
        }
        return "order_result";
    }

    /**
     * 查询订单详情
     * GET /orders/{orderNo}
     */
    @GetMapping("/orders/{orderNo}")
    public String getOrderDetail(
            @PathVariable("orderNo") String orderNo,
            Map<String, Object> model
    ) {
        OnlineShoppingOrder order = orderService.queryOrderByOrderNum(orderNo);
        model.put("order", order);

        if (order != null) {
            OnlineShoppingCommodity commodity =
                    commodityDao.selectCommodityById(order.getCommodityId());
            model.put("commodity", commodity);
        } else {
            model.put("commodity", null);
        }

        return "order_check";
    }

    /**
     * 支付订单
     * POST /orders/{orderNo}/pay
     */
    @PostMapping("/orders/{orderNo}/pay")
    public String payOrder(
            @PathVariable("orderNo") String orderNo,
            Map<String, Object> model
    ) {
        orderService.payOrder(orderNo);
        return getOrderDetail(orderNo, model);
    }
}
