package com.andrewdu.onlineshopping_du.db.dao.impls;


import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.mappers.OnlineShoppingCommodityMapper;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class OnlineShoppingCommodityDaoImpl implements OnlineShoppingCommodityDao {

    @Resource
    OnlineShoppingCommodityMapper onlineShoppingCommodityMapper;

    @Override
    public int insertCommodity(OnlineShoppingCommodity commodity) {
        return onlineShoppingCommodityMapper.insert(commodity);
    }

    @Override
    public OnlineShoppingCommodity selectCommodityById(long commodityId) {
        return onlineShoppingCommodityMapper.selectByPrimaryKey(commodityId);
    }

    @Override
    public List<OnlineShoppingCommodity> listCommodities() {
        return onlineShoppingCommodityMapper.listCommodities();
    }

    @Override
    public List<OnlineShoppingCommodity> listCommoditiesByUserId(Long userId) {
        return onlineShoppingCommodityMapper.listCommoditiesByUserId(userId);
    }

    @Override
    public int updateCommodity(OnlineShoppingCommodity commodity) {
        return onlineShoppingCommodityMapper.updateByPrimaryKey(commodity);
    }
}