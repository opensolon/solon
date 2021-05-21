package org.noear.captcha.solon.plugin.util;


import org.noear.solon.core.Aop;
import org.noear.solon.core.cache.CacheService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CacheUtil {
    private static CacheService cacheService;
    static {
        Aop.getAsyn(CacheService.class,bw->{
            cacheService = bw.raw();
        });
    }

    public static void set(String key, String value, long expiresInSeconds){
        cacheService.store(key,value,(int)expiresInSeconds);
    }

    public static void delete(String key){
        cacheService.remove(key);
    }

    public static boolean exists(String key){
        return cacheService.get(key) != null;
    }

    public static String get(String key){
        return (String) cacheService.get(key);
    }
}
