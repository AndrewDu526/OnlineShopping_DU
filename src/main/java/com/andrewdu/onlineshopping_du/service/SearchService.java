package com.andrewdu.onlineshopping_du.service;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
public class SearchService {
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @Resource
    EsService esService;

    public List<OnlineShoppingCommodity> searchCommodityWithMySQL(String keyword) {
        return  onlineShoppingCommodityDao.searchCommodityByKeyword(keyword);
    }

    public List<OnlineShoppingCommodity> searchCommodityWithEs(String keyword) throws IOException {
        return  esService.searchCommodity(keyword, 0 , 100);
    }
}
