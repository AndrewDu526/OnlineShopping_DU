package com.andrewdu.onlineshopping_du.component;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import com.andrewdu.onlineshopping_du.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

// redis缓存预热，spring初始化是redis同步数据库，获取商品和对应库存
@Component
@Slf4j
public class RedisPreHeatRunner implements ApplicationRunner {
    // 通过实现ApplicationRunner接口，做到spring初始化时自动执行run()方法
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
