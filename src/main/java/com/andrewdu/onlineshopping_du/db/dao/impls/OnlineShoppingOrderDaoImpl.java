package com.andrewdu.onlineshopping_du.db.dao.impls;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingOrderDao;
import com.andrewdu.onlineshopping_du.db.mappers.OnlineShoppingOrderMapper;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingOrder;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
@Repository
public class OnlineShoppingOrderDaoImpl implements OnlineShoppingOrderDao {
    @Resource
    OnlineShoppingOrderMapper onlineShoppingOrderMapper;

    @Override
    public int insertOrder(OnlineShoppingOrder order) {
        return onlineShoppingOrderMapper.insert(order);
    }

    @Override
    public OnlineShoppingOrder queryOrderByOrderNum(String orderNum) {
        return onlineShoppingOrderMapper.queryOrderByOrderNum(orderNum);
    }

    @Override
    public int updateOrder(OnlineShoppingOrder onlineShoppingOrder) {
        return onlineShoppingOrderMapper.updateByPrimaryKey(onlineShoppingOrder);
    }
}