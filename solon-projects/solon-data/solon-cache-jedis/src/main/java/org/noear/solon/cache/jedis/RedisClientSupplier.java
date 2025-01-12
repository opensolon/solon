/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
