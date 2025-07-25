package com.andrewdu.onlineshopping_du.controller;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class CommodityController {
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @RequestMapping("/addCommodity")
    public String addCommodity(){
        return "add_commodity";
    }

    @PostMapping("/addCommoditySuccess")
    public String addCommoditySuccess(
        @RequestParam("commodityId") Long commodityId,
        @RequestParam("commodityName") String commodityName,
        @RequestParam("commodityDesc") String commodityDesc,
        @RequestParam("price") int price,
        @RequestParam("availableStock") int availableStock,
        @RequestParam("creatorUserId") long creatorUserId,
        Map<String, Object> resultMap
    ){
        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .commodityId(commodityId)
                .commodityName(commodityName)
                .commodityDesc(commodityDesc)
                .price(price)
                .availableStock(availableStock)
                .creatorUserId(creatorUserId)
                .totalStock(availableStock)
                .lockStock(0)
                .build();

        onlineShoppingCommodityDao.insertCommodity(commodity);
        resultMap.put("Commodity", commodity);
        return "add_commodity_success";
    }

    @GetMapping("/commodities/{sellerId}")
    public String ListCommoditiesById(@PathVariable("sellerId") Long sellerId, Map<String, Object> resultMap){

        List<OnlineShoppingCommodity> onlineShoppingCommodities = onlineShoppingCommodityDao.listCommoditiesBySellerId(sellerId);

        resultMap.put("Commodities", onlineShoppingCommodities);

        return "list_commodities";
    }


    @GetMapping("/commodities")
    public String ListCommodities(Map<String, Object> resultMap){

        List<OnlineShoppingCommodity> onlineShoppingCommodities = onlineShoppingCommodityDao.listCommodities();

        resultMap.put("Commodities", onlineShoppingCommodities);

        return "list_commodities";
    }

    @GetMapping("commodity/{commodityId}")
    public String ListCommodityByCommodityId(@PathVariable("commodityId")  long commodityId,
                               Map<String, Object> resultMap) {
        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.ListCommodityByCommodityId(commodityId);
        resultMap.put("commodity", onlineShoppingCommodity);
        return "commodity_detail";
    }

}
