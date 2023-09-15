package org.noear.solon.extend.sqltoy.translate;

import org.noear.solon.data.cache.CacheService;
import org.sagacity.sqltoy.translate.cache.TranslateCacheManager;
import org.sagacity.sqltoy.translate.model.TranslateConfigModel;

import java.util.HashMap;

/**
 *  基于Solon CacheServices做的TranslateCache
 *  * @author 夜の孤城
 *  * @since 1.5
 */
public class SolonTranslateCacheManager extends TranslateCacheManager {
    static final String prefix="sqltoy.translate:";
    CacheService cacheService;

    public SolonTranslateCacheManager(CacheService cacheService){
       this.cacheService=cacheService;
    }
    private String buildKey(String cacheName,String cacheType){
        return prefix+":"+cacheName+":"+cacheType;
    }

    @Override
    public boolean hasCache(String s) {
        Object cache = cacheService.get(prefix + s, Boolean.class);
        return cache != null;
    }

    @Override
    public HashMap<String, Object[]> getCache(String cacheName, String cacheType) {
       return (HashMap<String, Object[]> )cacheService.get(buildKey(cacheName,cacheType), HashMap.class);
    }

    @Override
    public void put(TranslateConfigModel cacheModel, String cacheName, String cacheType, HashMap<String, Object[]> cacheValue) {
        cacheService.store(buildKey(cacheName,cacheType),cacheValue,cacheModel.getKeepAlive());
    }

    @Override
    public void clear(String cacheName, String cacheType) {
        cacheService.remove(buildKey(cacheName,cacheType));
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public void destroy() {

    }
}
