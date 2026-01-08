package com.andrewdu.onlineshopping_du.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.text.MessageFormat;
import java.time.Duration;

// TODO: 对比Jedis, Lettuce, 和 Spring Redis

@Configuration // “语义”注释：这个类里的代码，不是为了被业务反复调用执行，而是为了描述 Bean 的创建规则和依赖关系
               // spring 仍然会构造 @Configuration 注释的类（JedisConfig）的实例对象至IoC空间，但不推荐使用
@Slf4j
// @Slf4j Simple Logger Facade for java
// 是 Lombok(不是 spring) 提供的编译期注解，编译时 Lombok 会把这个注解“翻译”为一段标准的 Java 日志字段代码，在 .class 文件
// 中真实生成一个 Logger log 成员，故不存在动态创建 Logger 的过程。
public class JedisConfig {

    // TODO: 对比 @Value("${property.key}") 和 @ConfigurationProperties

    @Value("${spring.redis.host}") // 从配置文件application.properties中获取并注入指定的数值
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.pool.max-wait}")
    private long maxWaitMillis;
    @Value("${spring.redis.timeout}")
    private int timeout;

    /*
    * @Configuration + @Bean 通过 CGLIB 代理确保 @Bean 方法的单例行为
    * 即spring初始化时将执行@bean注释的方法，将返回值实例存放于IoC容器中
    * 1) 在依赖注入时，如@autowired 或 @resource 全局使用IoC中的实例
    * 2) 在此同一个@Configuration注释的类中，普通java方法调用了被@bean修饰的方法时不会按照既定的原方法新建实例返回，而是直接使用IoC中的实例。
    */

    @Bean // 某个方法负责创建一个对象，该对象应由 Spring IoC 容器管理
          // 用于在配置类中声明一个 Bean
          // 只能用于注释方法
    public JedisPool jedisPoolProvider() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWait(Duration.ofMillis(maxWaitMillis));
        config.setMaxTotal(maxActive);
        JedisPool jedisPool = new JedisPool(config, host, port, timeout);
        log.info("JedisPool 注入成功");
        log.info(MessageFormat.format("Redis address: {0}: {1}", host, port));
        return jedisPool;
    }
}