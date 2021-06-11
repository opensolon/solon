package org.noear.solon.data;

import org.noear.solon.core.cache.CacheService;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存标签管理器
 * */
public class CacheTags {
    private CacheService _cache;

    public CacheTags(CacheService caching) {
        _cache = caching;
    }

    /**
     * 为缓存添加一个标签
     *
     * @param tag            标签
     * @param targetCacheKey 目标缓存键
     */
    public void add(String tag, String targetCacheKey) {
        List<String> temp = $get(tagKey(tag));
        if (temp.contains(targetCacheKey))
            return;

        temp.add(targetCacheKey);

        $set(tagKey(tag), temp);
    }

    /**
     * 移除标签相关的所有缓存
     *
     * @param tag 缓存标签
     */
    public CacheTags remove(String tag) {
        List<String> keys = $get(tagKey(tag));

        for (String cacheKey : keys)
            _cache.remove(cacheKey);

        _cache.remove(tagKey(tag));

        return this;
    }

    /**
     * 更新标签相关的所有缓存
     * */
    public void update(String tag, Object newValue, int seconds) {
        List<String> keys = $get(tagKey(tag));

        for (String key : keys) {
            Object temp = _cache.get(key);
            if (temp != null) {
                //如果之前有缓存，则：
                //
                if (newValue == null) {
                    //如果值为null，则删除
                    _cache.remove(key);
                } else {
                    //类型一样才更新 //避免引起莫名的错
                    if (newValue.getClass() == temp.getClass()) {
                        _cache.store(key, newValue, seconds);
                    }
                }
            }
        }
    }

    protected List<String> $get(String tagKey) {
        Object temp = _cache.get(tagKey);

        if (temp == null)
            return new ArrayList<>();
        else
            return (List<String>) temp;
    }

    protected void $set(String tagKey, List<String> value) {
        _cache.store(tagKey, value, 0);
    }

    protected String tagKey(String tag) {
        return ("@" + tag).toUpperCase();
    }
}
