package com.andrewdu.onlineshopping_du.db.dao.impls;

import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingUserDao;
import com.andrewdu.onlineshopping_du.db.mappers.OnlineShoppingUserMapper;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingUser;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import java.util.List;

@Repository
public class OnlineShoppingUserDaoImpl implements OnlineShoppingUserDao
{
    @Resource
    OnlineShoppingUserMapper mapper;
    @Override
    public int deleteUserById(Long userId) {
        return mapper.deleteByPrimaryKey(userId);
    }
    @Override
    public int insertUser(OnlineShoppingUser user) {
        return mapper.insert(user);
    }
    @Override
    public OnlineShoppingUser queryUserById(Long userId) {
        return mapper.selectByPrimaryKey(userId);
    }
    @Override
    public int updateUser(OnlineShoppingUser user) {
        return mapper.updateByPrimaryKey(user);
    }

}
