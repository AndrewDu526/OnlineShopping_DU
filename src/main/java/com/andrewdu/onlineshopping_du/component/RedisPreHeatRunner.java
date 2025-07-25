package com.andrewdu.onlineshopping_du.component;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import com.andrewdu.onlineshopping_du.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class RedisPreHeatRunner implements ApplicationRunner {
    @Resource
    private RedisService redisService;

    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<OnlineShoppingCommodity> onlineShoppingCommodities =
                onlineShoppingCommodityDao.listCommodities();
        for  (OnlineShoppingCommodity commodity : onlineShoppingCommodities) {
            String redisKey = "online_shopping:online_shopping_commodity:stock:" + commodity.getCommodityId();
            redisService.setValue(redisKey, commodity.getAvailableStock().toString());
            log.info("preHeat Staring: Initialize commodity :" + commodity.getCommodityId());
        }
    }
}
