package org.noear.solon.data.cache;

import java.util.Properties;

/**
 * 缓存服务工厂
 *
 * @author noear
 * @since 1.6
 */
public interface CacheFactory {
    /**
     * 创建缓存服务
     * */
    CacheService create(Properties props);
}
