package org.noear.solon.data.cache;

import java.lang.reflect.Type;

/**
 * 空缓存服务
 *
 * @author noear
 * @since 2.8
 */
public class EmptyCacheService implements CacheService{
    @Override
    public void store(String key, Object obj, int seconds) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public <T> T get(String key, Type type) {
        return null;
    }
}
