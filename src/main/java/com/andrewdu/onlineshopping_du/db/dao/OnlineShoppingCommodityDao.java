package com.andrewdu.onlineshopping_du.db.dao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityDao {
    int insertCommodity(OnlineShoppingCommodity commodity);
    OnlineShoppingCommodity selectCommodityById(long commodityId);
    List<OnlineShoppingCommodity> listCommodities();
    List<OnlineShoppingCommodity> listCommoditiesByUserId(Long userId);
    int updateCommodity(OnlineShoppingCommodity commodity);
}
