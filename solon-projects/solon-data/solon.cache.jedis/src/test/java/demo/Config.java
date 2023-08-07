package demo;

import org.noear.redisx.RedisClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.jedis.RedisCacheService;
import org.noear.solon.cache.jedis.RedisClientSupplier;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.CacheServiceSupplier;

/**
 * @author noear
 * @since 1.5
 */
@Configuration
public class Config {
    @Bean(value = "cache1", typed = true)//默认
    public CacheService cache1(@Inject("${solon.cache1}") RedisCacheService cache) {
        return cache;
    }

    @Bean("cache2")
    public CacheService cache2(@Inject("${solon.cache2}") CacheServiceSupplier cacheSupplier) {
        return cacheSupplier.get();
    }

    @Bean("cache3")
    public RedisClient cache3(@Inject("${solon.cache2}") RedisClientSupplier clientSupplier) {
        return clientSupplier.get();
    }
}
