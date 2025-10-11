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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存服务库管理
 *
 * @author noear
 * @since 1.0
 * */
public class CacheLib {
    //
    // CacheExecutor 对接
    //
    private static Map<String, CacheService> cacheServiceMap = new HashMap<>();


    /**
     * 缓存服务集合；只读
     */
    public static Map<String, CacheService> cacheServiceMap() {
        return Collections.unmodifiableMap(cacheServiceMap);
    }

    /**
     * 添加缓存服务
     */
    public static void cacheServiceAdd(String name, CacheService cs) {
        cacheServiceMap.put(name, cs);
    }

    /**
     * 添加缓存服务
     * */
    public static void cacheServiceAddIfAbsent(String name, CacheService cs) {
        cacheServiceMap.putIfAbsent(name, cs);
    }

    /**
     * 获取缓存服务
     */
    public static CacheService cacheServiceGet(String name) {
        return cacheServiceMap.get(name);
    }


    ///////////////////////

    private static Map<String, CacheFactory> cacheFactoryMap = new HashMap<>();

    /**
     * 注册缓存工厂
     *
     * @since 1.6
     * */
    public static void cacheFactoryAdd(String driverType, CacheFactory factory) {
        cacheFactoryMap.put(driverType, factory);
    }

    /**
     * 获取缓存工厂
     *
     * @since 1.6
     * */
    public static CacheFactory cacheFactoryGet(String driverType) {
        return cacheFactoryMap.get(driverType);
    }
}
