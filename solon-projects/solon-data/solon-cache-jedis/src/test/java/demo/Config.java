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
package demo;

import org.noear.redisx.RedisClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.jedis.RedisCacheService;
import org.noear.solon.cache.jedis.RedisClientSupplier;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.CacheServiceSupplier;

/**
 * @author noear
 * @since 1.5
 */
@Configuration
public class Config {
    @Bean(value = "cache1", typed = true)//默认
    public CacheService cache1(@Inject("${solon.cache1}") RedisCacheService cache) {
        return cache;
    }

    @Bean("cache2")
    public CacheService cache2(@Inject("${solon.cache2}") CacheServiceSupplier cacheSupplier) {
        return cacheSupplier.get();
    }

    @Bean("cache3")
    public RedisClient cache3(@Inject("${solon.cache2}") RedisClientSupplier clientSupplier) {
        return clientSupplier.get();
    }
}
