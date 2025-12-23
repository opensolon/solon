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
package org.noear.solon.cache.jedis;

import org.noear.redisx.RedisClient;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.impl.JavabinSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Properties;

/**
 * Redis 封装的缓存服务
 *
 * @author noear
 * @since 1.3
 */
public class RedisCacheService implements CacheService {
    static final Logger log = LoggerFactory.getLogger(RedisCacheService.class);

    //重写时可能会用到
    protected String _cacheKeyHead;
    protected int _defaultSeconds;
    protected Serializer<String> _serializer = null;
    protected boolean _enableMd5key = true;

    protected final RedisClient client;

    /**
     * 启用 Md5 key（默认为 true）
     * */
    public RedisCacheService enableMd5key(boolean enable) {
        _enableMd5key = enable;
        return this;
    }

    /**
     * 配置序列化
     * */
    public RedisCacheService serializer(Serializer<String> serializer) {
        if (serializer != null) {
            this._serializer = serializer;
        }

        return this;
    }


    public RedisCacheService(RedisClient client, int defSeconds) {
        this(client, null, defSeconds);
    }

    public RedisCacheService(RedisClient client, String keyHeader, int defSeconds) {
        this.client = client;

        if (Utils.isEmpty(keyHeader)) {
            keyHeader = Solon.cfg().appName();
        }

        if (defSeconds < 1) {
            defSeconds = 30;
        }

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        _serializer = JavabinSerializer.instance;
    }

    public RedisCacheService(Properties prop) {
        this(prop, prop.getProperty("keyHeader"), 0);
    }

    public RedisCacheService(Properties prop, String keyHeader, int defSeconds) {
        String defSeconds_str = prop.getProperty("defSeconds");
        String db_str = prop.getProperty("db");
        String maxTotal_str = prop.getProperty("maxTotal");

        if (defSeconds == 0) {
            if (Utils.isNotEmpty(defSeconds_str)) {
                defSeconds = Integer.parseInt(defSeconds_str);
            }
        }

        int db = 0;
        int maxTotal = 200;

        if (Utils.isNotEmpty(db_str)) {
            db = Integer.parseInt(db_str);
        }

        if (Utils.isNotEmpty(maxTotal_str)) {
            maxTotal = Integer.parseInt(maxTotal_str);
        }

        if (Utils.isEmpty(keyHeader)) {
            keyHeader = Solon.cfg().appName();
        }

        if (defSeconds < 1) {
            defSeconds = 30;
        }

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        _serializer = JavabinSerializer.instance;

        client = new RedisClient(prop, db, maxTotal);
    }

    /**
     * 获取 RedisClient
     */
    public RedisClient client() {
        return client;
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        if (obj == null) {
            return;
        }

        String newKey = newKey(key);

        try {
            String val = _serializer.serialize(obj);

            if (seconds > 0) {
                client.open((ru) -> ru.key(newKey).expire(seconds).set(val));
            } else {
                client.open((ru) -> ru.key(newKey).expire(_defaultSeconds).set(val));
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public <T> T get(String key, Type type) {
        String newKey = newKey(key);
        String val = client.openAndGet((ru) -> ru.key(newKey).get());

        if (val == null) {
            return null;
        }

        try {
            return (T) _serializer.deserialize(val, type);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void remove(String key) {
        String newKey = newKey(key);

        client.open((ru) -> {
            ru.key(newKey).delete();
        });
    }


    protected String newKey(String key) {
        if (_cacheKeyHead == null) {
            if (_enableMd5key) {
                return Utils.md5(key);
            } else {
                return key;
            }
        } else {
            if (_enableMd5key) {
                return _cacheKeyHead + ":" + Utils.md5(key);
            } else {
                return _cacheKeyHead + ":" + key;
            }
        }
    }
}
