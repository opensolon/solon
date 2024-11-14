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
        Entity ent = _data.computeIfAbsent(block, k -> new Entity());
        ent.futureDel();

        ent.map.put(key, obj);

        ent.future = RunUtil.delay(() -> {
            _data.remove(block);
        }, _defaultSeconds * 1000);
    }

    public void delay(String block) {
        Entity ent = _data.get(block);
        if (ent != null) {
            ent.futureDel();

            ent.future = RunUtil.delay(() -> {
                _data.remove(block);
            }, _defaultSeconds * 1000);
        }
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
            ent.futureDel();

            _data.remove(block);
        }
    }

    //存储实体
    private static class Entity {
        public Map<String, Object> map = new ConcurrentHashMap<>();
        public Future future;

        protected void futureDel() {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        }
    }
}