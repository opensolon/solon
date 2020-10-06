package org.noear.solon.extend.data;

import org.noear.solon.annotation.XNote;
import org.noear.solon.core.CacheService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CacheLib {
    //
    // XCacheExecutor 对接
    //
    private static Map<String, CacheService> cacheServiceMap = new HashMap<>();


    /**
     * 缓存服务集合；只读
     */
    @XNote("缓存服务集合；只读")
    public static Map<String, CacheService> cacheServiceMap() {
        return Collections.unmodifiableMap(cacheServiceMap);
    }

    /**
     * 添加缓存服务
     */
    @XNote("添加缓存服务")
    public static void cacheServiceAdd(String name, CacheService cs) {
        cacheServiceMap.put(name, cs);
    }

    @XNote("添加缓存服务")
    public static void cacheServiceAddIfAbsent(String name, CacheService cs) {
        cacheServiceMap.putIfAbsent(name, cs);
    }

    /**
     * 获取缓存服务
     */
    @XNote("获取缓存服务")
    public static CacheService cacheServiceGet(String name) {
        return cacheServiceMap.get(name);
    }
}
