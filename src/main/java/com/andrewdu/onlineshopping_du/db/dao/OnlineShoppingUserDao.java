package com.andrewdu.onlineshopping_du.db.dao;

import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingUser;

import java.util.List;

public interface OnlineShoppingUserDao {
    int insertUser(OnlineShoppingUser user);

    OnlineShoppingUser queryUserById(Long userId);

    int updateUser(OnlineShoppingUser user);

    int deleteUserById(Long userId);
}