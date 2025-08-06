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

import org.noear.solon.Utils;
import org.noear.solon.core.util.RunUtil;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 默认缓存服务
 *
 * @author noear
 * @since 1.0
 * */
public class LocalCacheService implements CacheService {
    public static final CacheService instance = new LocalCacheService();

    private int _defaultSeconds;

    //缓存存储器
    private final Map<String, Entity> _data = new ConcurrentHashMap<>();
    private final ReentrantLock SYNC_LOCK = new ReentrantLock();


    public LocalCacheService() {
        this(30);
    }

    public LocalCacheService(int defSeconds) {
        _defaultSeconds = defSeconds;
    }

    public LocalCacheService(Properties prop) {
        String defSeconds_str = prop.getProperty("defSeconds");

        if (Utils.isNotEmpty(defSeconds_str)) {
            _defaultSeconds = Integer.parseInt(defSeconds_str);
        }

        if (_defaultSeconds < 1) {
            _defaultSeconds = 30;
        }
    }

    /**
     * 保存
     *
     * @param key     缓存键
     * @param obj     对象
     * @param seconds 秒数
     */
    @Override
    public void store(String key, Object obj, int seconds) {
        if (seconds <= 0) {
            seconds = getDefalutSeconds();
        }

        SYNC_LOCK.lock();
        try {
            Entity ent = _data.get(key);
            if (ent == null) {
                //如果末存在，则新建实体
                ent = new Entity(obj);
                _data.put(key, ent);
            } else {
                //如果已存储，取消超时处理，且更新值
                ent.value = obj;
                ent.futureDel();
            }

            if (seconds > 0) {
                //设定新的超时
                ent.future = RunUtil.delay(() -> {
                    _data.remove(key);
                }, seconds * 1000L);
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 获取
     *
     * @param key 缓存键
     */
    @Override
    public <T> T get(String key, Type type) {
        Entity ent = _data.get(key);

        return ent == null ? null : (T) ent.value;
    }

    /**
     * 移除
     *
     * @param key 缓存键
     */
    @Override
    public void remove(String key) {
        SYNC_LOCK.lock();
        try {
            Entity ent = _data.remove(key);

            if (ent != null) {
                ent.futureDel();
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    /**
     * 表空
     */
    public void clear() {
        for (Entity ent : _data.values()) {
            ent.futureDel();
        }

        _data.clear();
    }

    public int getDefalutSeconds() {
        return _defaultSeconds;
    }


    /**
     * 存储实体
     */
    private static class Entity {
        public Object value;
        public Future future;

        public Entity(Object val) {
            this.value = val;
        }

        protected void futureDel() {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        }
    }
}
