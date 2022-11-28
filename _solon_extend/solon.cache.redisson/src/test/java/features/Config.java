package features;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.redisson.RedissonCacheService;
import org.noear.solon.cache.redisson.RedissonClientSupplier;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.CacheServiceSupplier;
import org.redisson.api.RedissonClient;

/**
 * @author noear 2022/11/28 created
 */
@Configuration
public class Config {
    //构建 CacheService
    @Bean
    public CacheService cache1(@Inject("${test.rd1}") RedissonCacheService cache) {
        //可以拿到内部的 client
        //cache.client();

        return cache;
    }

    //构建 CacheService
    @Bean
    public CacheService cache2(@Inject("${test.rd1}") CacheServiceSupplier cacheSupplier) {
        return cacheSupplier.get();
    }

    //构建 RedissonClient
    @Bean
    public RedissonClient cache3(@Inject("${test.rd1}") RedissonClientSupplier clientSupplier) {
        return clientSupplier.get();
    }
}
