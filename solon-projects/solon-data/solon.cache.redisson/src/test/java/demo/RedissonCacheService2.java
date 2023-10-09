package demo;

import org.noear.solon.Utils;
import org.noear.solon.cache.redisson.RedissonCacheService;

import java.util.Properties;

/**
 * @author noear 2023/10/9 created
 */
public class RedissonCacheService2 extends RedissonCacheService {
    public RedissonCacheService2(Properties prop) {
        super(prop);
    }

    @Override
    protected String newKey(String key) {
        return _cacheKeyHead + ":" + key;
    }
}
