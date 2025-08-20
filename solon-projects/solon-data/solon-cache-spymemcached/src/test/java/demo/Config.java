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
import org.noear.solon.cache.spymemcached.MemCacheService;
import org.noear.solon.data.cache.CacheService;

/**
 * @author noear
 * @since 1.5
 */
@Configuration
public class Config {
    @Managed(value = "cache1", typed = true) //默认
    public CacheService cache1(@Inject("${solon.cache1}") MemCacheService cache){
        return cache;
    }

    @Managed("cache2")
    public CacheService cache2(@Inject("${solon.cache2}") MemCacheService cache){
        return cache;
    }
}
