package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.spymemcached.MemCacheService;
import org.noear.solon.data.cache.CacheService;

/**
 * @author noear
 * @since 1.5
 */
@Configuration
public class Config {
    @Bean(value = "cache1", typed = true) //默认
    public CacheService cache1(@Inject("${solon.cache1}") MemCacheService cache){
        return cache;
    }

    @Bean("cache2")
    public CacheService cache2(@Inject("${solon.cache2}") MemCacheService cache){
        return cache;
    }
}
