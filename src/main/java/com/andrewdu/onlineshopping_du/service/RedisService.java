package com.andrewdu.onlineshopping_du.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;
@Service
@Slf4j
public class RedisService {
    @Resource
    private JedisPool jedisPool;

    public String getValue(String key){
        Jedis jedis = jedisPool.getResource();
        String res = jedis.get(key);
        jedis.close();
        return res;
    }

    public String setValue(String key, String value){
        Jedis jedis = jedisPool.getResource();
        String res = jedis.set(key, value);
        jedis.close();
        return res;
    }

    public long deductStockWithCommodityId(String key) {
        // query , then deduct, merge into one operation, lua
        Jedis jedis = jedisPool.getResource();
        String script =
                "if redis.call('exists', KEYS[1]) == 1 then\n" +
                        "    local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                        "    if (stock<=0) then\n" +
                        "        return -1\n" +
                        "    end\n" +
                        "\n" +
                        "    redis.call('decr', KEYS[1]);\n" +
                        "    return stock - 1;\n" +
                        "end\n" +
                        "\n" +
                        "return -1;";
        Long stock = -1L;
        try {
            stock = (Long)jedis.eval(script, Collections.singletonList(key), Collections.emptyList());
        } catch (Exception e) {
            log.error("Redis failed on stockDeduct", e);
        } finally {
            jedis.close();
        }
        return stock;
    }
}
