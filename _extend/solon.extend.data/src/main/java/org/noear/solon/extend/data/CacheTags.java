package org.noear.solon.extend.data;

import org.noear.solon.annotation.XNote;
import org.noear.solon.core.CacheService;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存标签管理器
 * */
public class CacheTags {
    private CacheService _Cache;

    public CacheTags(CacheService caching) {
        _Cache = caching;
    }

    /**
     * 为缓存添加一个标签
     *
     * @param tag            标签
     * @param targetCacheKey 目标缓存键
     */
    @XNote("为缓存添加一个标签")
    public void add(String tag, String targetCacheKey) {
        List<String> temp = $get(tagKey(tag));
        if (temp.contains(targetCacheKey))
            return;

        temp.add(targetCacheKey);

        $set(tagKey(tag), temp);
    }

    /**
     * 清空标签相关的所有缓存
     *
     * @param tag 缓存标签
     */
    @XNote("清空标签相关的所有缓存")
    public CacheTags clear(String tag) {
        List<String> keys = $get(tagKey(tag));

        for (String cacheKey : keys)
            _Cache.remove(cacheKey);

        _Cache.remove(tagKey(tag));

        return this;
    }

    protected List<String> $get(String tagKey) {
        Object temp = _Cache.get(tagKey);

        if (temp == null)
            return new ArrayList<>();
        else
            return (List<String>) temp;
    }

    protected void $set(String tagKey, List<String> value) {
        _Cache.store(tagKey, value, 0);
    }

    protected String tagKey(String tag) {
        return ("@" + tag).toUpperCase();
    }
}
