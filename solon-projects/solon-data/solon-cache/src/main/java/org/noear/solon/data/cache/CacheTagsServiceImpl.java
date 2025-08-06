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
import java.util.ArrayList;
import java.util.List;

/**
 * 支持标签的缓存服务实现
 *
 * @author 胡高
 * @since 1.10
 */
public class CacheTagsServiceImpl implements CacheTagsService {

    private static final String TAG_SECONDS = "{{s}}:";

    private CacheService _cache;

    public CacheTagsServiceImpl(CacheService caching) {
        this._cache = caching;
    }

    /**
     * 获取标签键列表
     *
     * @param tag 缓存标签
     */
    protected List<String> _get(String tag) {
        Object temp = this._cache.get(tag, ArrayList.class);

        if (temp == null) {
            return new ArrayList<>();
        } else {
            return (List<String>) temp;
        }
    }

    /**
     * 设置缓存键列表
     *
     * @param tag     缓存标签
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
    public <T> T get(String key, Type type) {
        return this._cache.get(key, type);
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
     * @param tag        标签
     * @param key        缓存键
     * @param refSeconds 缓存时间
     */
    protected void update(String key, String tag, Object newValue, int refSeconds) {
        String tagKey = this._tagKey(tag);

        List<String> cacheKeyList = this._get(tagKey);
        if (cacheKeyList.contains(key)) {
            if (newValue == null) {
                // 如果值为null，则删除
                this._cache.remove(key);
            } else {
                Object temp = this._cache.get(key, newValue.getClass());

                if (temp != null) {
                    // 如果之前有缓存，则改 // 类型一样才更新 //避免引起莫名的错
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
            } else {
                // 不存在时间时
                cacheKeyList.add(0, TAG_SECONDS + seconds);
            }
        } else {
            // 第一次时
            cacheKeyList.add(0, TAG_SECONDS + seconds);
        }

        cacheKeyList.add(key);

        this._set(tagKey, cacheKeyList, seconds);
    }
}
