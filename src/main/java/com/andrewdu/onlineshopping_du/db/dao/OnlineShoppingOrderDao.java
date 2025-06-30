package com.andrewdu.onlineshopping_du.db.dao;

import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingOrder;

public interface OnlineShoppingOrderDao {
    int insertOrder(OnlineShoppingOrder order);
    OnlineShoppingOrder queryOrderByOrderNum(String orderNum);

    int updateOrder(OnlineShoppingOrder onlineShoppingOrder);
}
