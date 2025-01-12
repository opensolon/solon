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
package org.noear.solon.data.cache;

import java.lang.reflect.Type;

/**
 * 二级缓存服务
 *
 * @author noear
 * @since 1.2
 * */
public class SecondCacheService implements CacheService {
    private CacheService cache1;
    private CacheService cache2;
    private int bufferSeconds;


    /**
     * @param cache1 一级缓存
     * @param cache2 二级缓存
     */
    public SecondCacheService(CacheService cache1, CacheService cache2) {
        this(cache1, cache2, 5);
    }

    /**
     * @param cache1        一级缓存
     * @param cache2        二级缓存
     * @param bufferSeconds 缓冲秒数
     */
    public SecondCacheService(CacheService cache1, CacheService cache2, int bufferSeconds) {
        this.cache1 = cache1;
        this.cache2 = cache2;
        this.bufferSeconds = bufferSeconds;
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        cache1.store(key, obj, seconds);
        cache2.store(key, obj, seconds);
    }

    @Override
    public <T> T get(String key, Type type) {
        T temp = cache1.get(key, type);
        if (temp == null) {
            temp = cache2.get(key, type);
            if (bufferSeconds > 0 && temp != null) {
                cache1.store(key, temp, bufferSeconds);
            }
        }
        return temp;
    }

    @Override
    public void remove(String key) {
        cache2.remove(key);
        cache1.remove(key);
    }
}
