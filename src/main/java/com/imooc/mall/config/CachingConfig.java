package com.imooc.mall.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

/**
 * 缓存的配置类
 */
@Configuration
@EnableCaching
public class CachingConfig {
    // 构造方法注入bean
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory){
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(connectionFactory);
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        // 超时时间
        cacheConfiguration = cacheConfiguration.entryTtl(Duration.ofSeconds(30));
        return new RedisCacheManager(redisCacheWriter, cacheConfiguration);
    }
}
