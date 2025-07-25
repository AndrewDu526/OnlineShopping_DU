package com.andrewdu.onlineshopping_du.db.dao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityDao {
    int insertCommodity(OnlineShoppingCommodity commodity);
    OnlineShoppingCommodity ListCommodityByCommodityId(long commodityId);
    List<OnlineShoppingCommodity> listCommodities();
    List<OnlineShoppingCommodity> listCommoditiesBySellerId(Long sellerId);
    int updateCommodity(OnlineShoppingCommodity commodity);
    int deductStockWithCommodityId(long commodityId);
}
