package demo;

import org.noear.solon.Utils;
import org.noear.solon.cache.spymemcached.MemCacheService;

import java.util.Properties;

/**
 * @author noear 2023/10/9 created
 */
public class MemCacheService2 extends MemCacheService {
    public MemCacheService2(Properties prop) {
        super(prop);
    }

    @Override
    protected String newKey(String key) {
        return _cacheKeyHead + "$" + key;
    }
}
