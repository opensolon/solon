package features;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.redisson.RedissonCacheService;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.impl.JsonSerializer;

/**
 * @author noear 2022/11/28 created
 */
@Configuration
public class CacheConfig {
    @Bean
    public CacheService cache1(@Inject("${test.rd1}") RedissonCacheService cache) {
        cache.serializer(JsonSerializer.instance);
        return cache;
    }
}
