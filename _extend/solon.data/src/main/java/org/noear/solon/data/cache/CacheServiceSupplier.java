package org.noear.solon.data.cache;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * CacheService 供应者，根据注册的工厂获取对应的缓存服务
 *
 * @author noear
 * @since 1.5
 */
public class CacheServiceSupplier implements Supplier<CacheService> {
    private CacheService real;
    private String driverType;

    public CacheServiceSupplier(Properties props) {
        driverType = props.getProperty("driverType");

        CacheFactory factory = CacheLib.cacheFactoryGet(driverType);

        if (factory != null) {
            real = factory.create(props);
        } else {
            throw new IllegalArgumentException("There is no supported driverType");
        }
    }

    @Override
    public CacheService get() {
        return real;
    }
}
