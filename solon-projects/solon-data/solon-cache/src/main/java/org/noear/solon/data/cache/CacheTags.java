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

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存标签管理器
 *
 * @author noear
 * @since 1.0
 * */
public class CacheTags {
    private static final String TAG_SECONDS  ="{{s}}:";
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
    public void add(String tag, String targetCacheKey, int refSeconds) {
        String tagKey = _tagKey(tag);

        List<String> cacheKeyList = _get(tagKey);
        if (cacheKeyList.contains(targetCacheKey))
            return;

        int seconds = refSeconds;
        if (cacheKeyList.size() > 0) {
            String secondsStr = cacheKeyList.get(0);
            if (secondsStr.startsWith(TAG_SECONDS)) {
                seconds = Integer.parseInt(secondsStr.substring(TAG_SECONDS.length()));

                if (refSeconds > seconds) {
                    seconds = refSeconds;
                    cacheKeyList.remove(0);
                    //时间不同时
                    cacheKeyList.add(0, TAG_SECONDS + seconds);
                }
            }else{
                //不存在时间时
                cacheKeyList.add(0, TAG_SECONDS + seconds);
            }
        }else{
            //第一次时
            cacheKeyList.add(0, TAG_SECONDS + seconds);
        }


        cacheKeyList.add(targetCacheKey);

        _set(tagKey, cacheKeyList, seconds);
    }

    /**
     * 移除标签相关的所有缓存
     *
     * @param tag 缓存标签
     */
    public CacheTags remove(String tag) {
        String tagKey = _tagKey(tag);

        List<String> cacheKeyList = _get(tagKey);

        for (String cacheKey : cacheKeyList) {
            if (cacheKey.startsWith(TAG_SECONDS) == false) {
                _cache.remove(cacheKey);
            }
        }

        _cache.remove(tagKey);

        return this;
    }

    /**
     * 更新标签相关的所有缓存
     *
     * @param tag 缓存标签
     * @param newValue 新的值
     * @param seconds 秒数
     * */
    public void update(String tag, Object newValue, int seconds) {
        String tagKey = _tagKey(tag);

        List<String> cacheKeyList = _get(tagKey);

        for (String cacheKey : cacheKeyList) {
            if (cacheKey.startsWith(TAG_SECONDS) == false) {
                if (newValue == null) {
                    //如果值为null，则删除
                    _cache.remove(cacheKey);
                } else {
                    Object temp = _cache.get(cacheKey, newValue.getClass());

                    if (temp != null) {
                        //如果之前有缓存，则改 //类型一样才更新 //避免引起莫名的错
                        if (newValue.getClass() == temp.getClass()) {
                            _cache.store(cacheKey, newValue, seconds);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取缓存键列表
     *
     * @param tagKey 标签键
     * */
    protected List<String> _get(String tagKey) {
        Object temp = _cache.get(tagKey, ArrayList.class);

        if (temp == null)
            return new ArrayList<>();
        else
            return (List<String>) temp;
    }

    /**
     * 设置缓存键列表
     *
     * @param tagKey 标签键
     * @param value 标签键列表
     * */
    protected void _set(String tagKey, List<String> value, int seconds) {
        _cache.store(tagKey, value, seconds);
    }

    /**
     * 生成标签键
     * */
    protected String _tagKey(String tag) {
        return ("@" + tag).toUpperCase();
    }
}
