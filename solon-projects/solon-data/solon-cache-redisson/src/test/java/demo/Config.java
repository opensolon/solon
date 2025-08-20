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

import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.redisson.RedissonBuilder;
import org.noear.solon.cache.redisson.RedissonCacheService;
import org.noear.solon.cache.redisson.RedissonClientSupplier;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.CacheServiceSupplier;
import org.redisson.api.RedissonClient;

import java.util.Properties;

/**
 * @author noear 2022/11/28 created
 */
@Configuration
public class Config {
    //构建 CacheService
    @Managed
    public CacheService cache1(@Inject("${test.rd1}") RedissonCacheService cache) {
        //可以拿到内部的 client
        //cache.client();

        return cache;
    }

    //构建 CacheService
    @Managed
    public CacheService cache2(@Inject("${test.rd1}") CacheServiceSupplier cacheSupplier) {
        return cacheSupplier.get();
    }

    //构建 RedissonClient
    @Managed
    public RedissonClient cache3(@Inject("${test.rd1}") RedissonClientSupplier clientSupplier) {
        return clientSupplier.get();
    }


    @Managed
    public RedissonClient demo4(@Inject("${test.rd1}") Properties prop) {
        return RedissonBuilder.build(prop);
    }
}
