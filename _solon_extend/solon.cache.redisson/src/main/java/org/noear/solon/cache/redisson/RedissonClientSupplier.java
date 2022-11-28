package org.noear.solon.cache.redisson;

import org.redisson.api.RedissonClient;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author noear
 * @since 1.11
 */
public class RedissonClientSupplier implements Supplier<RedissonClient> {
    RedissonClient real;

    public RedissonClientSupplier(Properties props) {
        real = RedissonBuilder.build(props);
    }

    @Override
    public RedissonClient get() {
        return null;
    }
}
