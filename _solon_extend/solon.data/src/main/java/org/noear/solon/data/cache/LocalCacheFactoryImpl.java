package org.noear.solon.data.cache;

import java.util.Properties;

/**
 * @author noear
 * @since 1.6
 */
public class LocalCacheFactoryImpl implements CacheFactory{
    @Override
    public CacheService create(Properties props) {
        return new LocalCacheService(props);
    }
}
