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
    @Bean
    public CacheService cache1(@Inject("${solon.cache1}") MemCacheService cache){
        return cache;
    }

    @Bean
    public CacheService cache2(@Inject("${solon.cache2}") MemCacheService cache){
        return cache;
    }
}
