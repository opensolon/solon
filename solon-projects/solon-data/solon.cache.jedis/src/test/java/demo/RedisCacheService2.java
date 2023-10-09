package demo;

import org.noear.solon.Utils;
import org.noear.solon.cache.jedis.RedisCacheService;

import java.util.Properties;

/**
 * @author noear 2023/10/9 created
 */
public class RedisCacheService2 extends RedisCacheService {
    public RedisCacheService2(Properties prop) {
        super(prop);
    }

    @Override
    protected String newKey(String key) {
        return _cacheKeyHead + ":" + key;
    }
}
