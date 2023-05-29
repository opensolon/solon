package features;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.jedis.RedisCacheService;
import org.noear.solon.data.cache.CacheService;

/**
 * @author noear 2022/11/28 created
 */
@Configuration
public class CacheConfig {
    @Bean
    public CacheService cache1(@Inject("${test.rd1}") RedisCacheService cache) {
        return cache;
    }
}
