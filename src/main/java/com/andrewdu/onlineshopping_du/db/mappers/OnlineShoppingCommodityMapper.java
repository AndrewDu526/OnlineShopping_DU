package com.andrewdu.onlineshopping_du.db.mappers;

import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityMapper {
    int deleteByPrimaryKey(Long commodityId);

    int insert(OnlineShoppingCommodity record);

    int insertSelective(OnlineShoppingCommodity record);

    OnlineShoppingCommodity selectByPrimaryKey(Long commodityId);

    int updateByPrimaryKeySelective(OnlineShoppingCommodity record);

    int updateByPrimaryKey(OnlineShoppingCommodity record);

    List<OnlineShoppingCommodity> listCommodities();

    List<OnlineShoppingCommodity> listCommoditiesByUserId(Long userId);

    int deductStockWithCommodityId(Long commodityId);

    int revertStockWithCommodityId(long commodityId);

    List<OnlineShoppingCommodity> searchCommodityByKeyword(String keyword);
}