/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.sessionstate.local;

import org.noear.solon.core.util.RunUtil;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 定时存储器（做为Session存储方案）
 * */
class ScheduledStore {
    private int _defaultSeconds;

    private Map<String, Entity> _data = new ConcurrentHashMap<>();   //缓存存储器

    public ScheduledStore(int seconds) {
        _defaultSeconds = seconds;
    }


    public void put(String block, String key, Object obj) {
        Entity ent = _data.computeIfAbsent(block, k -> new Entity(k));
        ent.map.put(key, obj);
    }

    public Object get(String block, String key) {
        Entity ent = _data.get(block);
        if (ent != null) {
            return ent.map.get(key);
        }

        return null;
    }

    public Collection<String> getKeys(String block) {
        Entity ent = _data.get(block);
        if (ent != null) {
            return ent.map.keySet();
        }

        return null;
    }

    public void remove(String block, String key) {
        Entity ent = _data.get(block);
        if (ent != null) {
            ent.map.remove(key);
        }
    }

    public void clear(String block) {
        Entity ent = _data.get(block);
        if (ent != null) {
            ent.invalid();

            _data.remove(block);
        }
    }

    public long creationTime(String block) {
        Entity ent = _data.computeIfAbsent(block, k -> new Entity(k));
        if (ent != null) {
            return ent.creationTime;
        } else {
            return 0L;
        }
    }

    public long lastAccessTime(String block) {
        Entity ent = _data.get(block);
        if (ent != null) {
            return ent.lastAccessTime;
        } else {
            return 0L;
        }
    }

    public void updateAccessedTime(String block) {
        Entity ent = _data.get(block);
        if (ent != null) {
            ent.lastAccessTime = System.currentTimeMillis();

            ent.delay(_data, _defaultSeconds);
        }
    }

    //存储实体
    public static class Entity {
        public final String block;
        public final Map<String, Object> map = new ConcurrentHashMap<>();
        public long creationTime = System.currentTimeMillis();
        public long lastAccessTime = creationTime;
        private Future _future;

        public Entity(String block) {
            this.block = block;
        }

        protected void delay(Map<String, Entity> data, int seconds) {
            invalid();

            _future = RunUtil.delay(() -> {
                data.remove(block);
            }, seconds * 1000);
        }

        protected void invalid() {
            if (_future != null) {
                _future.cancel(true);
                _future = null;
            }
        }
    }
}