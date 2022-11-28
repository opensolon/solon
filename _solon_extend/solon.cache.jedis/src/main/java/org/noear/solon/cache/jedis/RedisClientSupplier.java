package org.noear.solon.cache.jedis;

import org.noear.redisx.RedisClient;
import org.noear.solon.Utils;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * @author noear
 * @since 1.11
 */
public class RedisClientSupplier implements Supplier<RedisClient> {
    private RedisClient real;

    public RedisClientSupplier(Properties props) {
        int db = 0;
        int maxTotal = 200;

        String db_str = props.getProperty("db");
        String maxTotal_str = props.getProperty("maxTotal");

        if (Utils.isNotEmpty(db_str)) {
            db = Integer.parseInt(db_str);
        }

        if (Utils.isNotEmpty(maxTotal_str)) {
            maxTotal = Integer.parseInt(maxTotal_str);
        }

        real = new RedisClient(props, db, maxTotal);
    }

    @Override
    public RedisClient get() {
        return real;
    }
}
