package org.noear.solon.data.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 胡高
 */
@SuppressWarnings("unchecked")
public class CacheTagsServiceImpl implements CacheTagsService {

    private static final String TAG_SECONDS  ="{{s}}:";

    private CacheService _cache;

    public CacheTagsServiceImpl(CacheService caching) {
        this._cache = caching;
    }

    /**
     * 获取标签键列表
     *
     * @param tag 缓存标签
     * */
    protected List<String> _get(String tag) {
        Object temp = this._cache.get(tag);

        if (temp == null) {
            return new ArrayList<>();
        } else {
            return (List<String>) temp;
        }
    }

    /**
     * 设置缓存键列表
     *
     * @param tag 缓存标签
     * @param keyList 标签键列表
     */
    protected void _set(String tag, List<String> keyList, int seconds) {
        this._cache.store(tag, keyList, seconds);
    }

    /**
     * 生成标签键
     * 
     * @param tag 标签键
     */
    protected String _tagKey(String tag) {
        return ("@" + tag).toUpperCase();
    }

    @Override
    public Object get(String key) {
        return this._cache.get(key);
    }

    @Override
    public void remove(String key) {
        this._cache.remove(key);
    }

    @Override
    public void removeTag(String... tags) {
        for (String tag : tags) {

            String tagKey = this._tagKey(tag);

            List<String> cacheKeyList = this._get(tagKey);

            for (String cacheKey : cacheKeyList) {
                if (cacheKey.startsWith(TAG_SECONDS) == false) {
                    this._cache.remove(cacheKey);
                }
            }

            this._cache.remove(tagKey);
        }
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        this._cache.store(key, obj, seconds);
    }

    @Override
    public void storeTag(String key, Object obj, int seconds, String... tags) {
        this._cache.store(key, obj, seconds);
        for (String tag : tags) {
            this.update(key, tag, obj, seconds);
        }
    }


    /**
     * 为缓存添加一个标签
     *
     * @param tag 标签
     * @param key 缓存键
     * @param refSeconds 缓存时间
     */
    protected void update(String key, String tag, Object newValue, int refSeconds) {
        String tagKey = this._tagKey(tag);

        List<String> cacheKeyList = this._get(tagKey);
        if (cacheKeyList.contains(key)) {
            Object temp = this._cache.get(key);

            if (temp != null) {
                // 如果之前有缓存，则：
                if (newValue == null) {
                    // 如果值为null，则删除
                    this._cache.remove(key);
                } else {
                    // 类型一样才更新 //避免引起莫名的错
                    if (newValue.getClass() == temp.getClass()) {
                        this._cache.store(key, newValue, refSeconds);
                    }
                }
            }
        }

        int seconds = refSeconds;
        if (cacheKeyList.size() > 0) {
            String secondsStr = cacheKeyList.get(0);
            if (secondsStr.startsWith(TAG_SECONDS)) {
                seconds = Integer.parseInt(secondsStr.substring(TAG_SECONDS.length()));

                if (refSeconds > seconds) {
                    seconds = refSeconds;
                    cacheKeyList.remove(0);
                    // 时间不同时
                    cacheKeyList.add(0, TAG_SECONDS + seconds);
                }
            }else{
                // 不存在时间时
                cacheKeyList.add(0, TAG_SECONDS + seconds);
            }
        }else{
            // 第一次时
            cacheKeyList.add(0, TAG_SECONDS + seconds);
        }

        cacheKeyList.add(key);

        this._set(tagKey, cacheKeyList, seconds);
    }

}
