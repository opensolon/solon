package demo;

import org.noear.solon.cache.spymemcached.MemCacheService;

import java.util.Properties;

public class MemCacheService2 extends MemCacheService {
    public MemCacheService2(Properties prop) {
        super(prop);
    }

    @Override
    protected String newKey(String key) {
        return _cacheKeyHead + "$" + key;
    }
}
