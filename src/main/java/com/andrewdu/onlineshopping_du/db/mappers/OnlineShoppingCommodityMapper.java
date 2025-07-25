package com.andrewdu.onlineshopping_du.db.mappers;

import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityMapper {
    int insert(OnlineShoppingCommodity record);
    OnlineShoppingCommodity ListCommodityByCommodityId(Long commodityId);
    List<OnlineShoppingCommodity> listCommodities();
    List<OnlineShoppingCommodity> listCommoditiesBySellerId(Long userId);
    int updateByPrimaryKey(OnlineShoppingCommodity record);
    int deductStockWithCommodityId(Long commodityId);
}