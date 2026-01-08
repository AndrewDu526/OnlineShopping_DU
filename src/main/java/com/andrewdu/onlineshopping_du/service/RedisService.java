package com.andrewdu.onlineshopping_du.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;
@Service // 业务层面
@Slf4j
public class RedisService {

    @Resource // 只能标注在字段 和 Setter 方法上，不支持构造器注入
              // @Resource(name="beanName") 按名称查找bean注入，若不提供name则使用字段名查找，做找不到使用字段类型；@Autowired默认按类型
    private JedisPool jedisPool;

    public RedisService setValue(String key, Long value) {
        Jedis client = jedisPool.getResource();
        client.set(key, value.toString());
        client.close();
        return this;
    }
    public void setValue(String key, String value) {
        Jedis jedisClient = jedisPool.getResource();
        jedisClient.set(key, value);
        jedisClient.close();
    }
    public String getValue(String key) {
        Jedis client = jedisPool.getResource();
        String value = client.get(key);
        client.close();
        return value;
    }

    // 这里利用了redis的单线程循环事件的特点和lua结合实现了高并发下原子性操作来解决超卖问题，并没有使用redis最主要的功能，即缓存
    public long stockDeduct(String key) {
        try (Jedis client = jedisPool.getResource()) {
            // 使用lua脚本包装：1)获取指定商品的库存 2)判断库存是否大于0 3)对库存减一 4)返回新库存
            // 这种情况下保证只有在商品有库存才会创建订单，避免超卖
            // lua将多条redis命令和判断逻辑合并为一条redis命令
            // 而redis做到单条redis命令原子性
            String script =
                    "if redis.call('exists', KEYS[1]) == 1 then\n" +
                    " local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                    " if (stock<=0) then\n" +
                    " return -1\n" +
                    " end\n" +
                    "\n" +
                    " redis.call('decr', KEYS[1]);\n" +
                    " return stock - 1;\n" +
                    "end\n" +
                    "\n" +
                    "return -1;";
            Long stock = (Long) client.eval(script, Collections.singletonList(key), Collections.emptyList());
            if (stock < 0) {
                System.out.println("There is no stock available");
                return -1;
            } else {
                System.out.println("Validate and decreased redis stock, current available stock： " + stock);
                return stock;
            }
        } catch (Throwable throwable) {
            System.out.println("Exception on stockDeductValidation： " + throwable.toString());
            return -1;
        }
    }

    public boolean tryToGetDistributedLock(String lockKey, String requestId, int expireTime) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.set(lockKey, requestId, "NX", "PX", expireTime);
        jedis.close();
        return "OK".equals(result);
    }

    public boolean releaseDistributedLock(String lockKey, String requestId) {
        Jedis jedis = jedisPool.getResource();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                " then return redis.call('del', KEYS[1])" +
                " else return 0 end";
        Long res = (Long)jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
        jedis.close();
        return res == 1L;
    }

    public long revertStock(String redisKey) {
        Jedis jedisClient = jedisPool.getResource();
        Long incr = jedisClient.incr(redisKey);
        jedisClient.close();
        return incr;
    }

    public void addToDenyList(String userId, String commodityId) {
        // key: online_shopping:denyListUserId:3
        // value: set of commodityId {1539,123,134,133}
        String key = "online_shopping:denyListUserId:" + userId;
        Jedis jedis = jedisPool.getResource();
        jedis.sadd(key, commodityId);
        jedis.close();
        log.info("Add userId: {} into denyList for commodityId: {}", userId, commodityId);
    }
    public boolean isInDenyList(String userId, String commodityId) {
        String key = "online_shopping:denyListUserId:" + userId;
        Jedis jedis = jedisPool.getResource();
        Boolean isInDenyList = jedis.sismember(key, commodityId);
        jedis.close();
        return isInDenyList;
    }

    public void removeFromDenyList(String userId, String commodityId) {
        String key = "online_shopping:denyListUserId:" + userId;
        Jedis jedis = jedisPool.getResource();
        jedis.srem(key, commodityId);
        jedis.close();
        log.info("Remove userId: {} into denyList for commodityId: {}", userId, commodityId);
    }
}

