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
package org.slf4j.impl;

import org.noear.solon.Utils;
import org.noear.solon.core.FactoryManager;
import org.slf4j.spi.MDCAdapter;

import java.util.*;

/**
 * @author noear
 * @since 1.11
 */
public class SolonMDCAdapter implements MDCAdapter {
    //改为懒加载
    private static ThreadLocal<Map<String, Deque<String>>> _threadMapOfStacks;
    private static ThreadLocal<Map<String, String>> _threadMap;

    private static ThreadLocal<Map<String, String>> threadMap() {
        if (_threadMap == null) {
            Utils.locker().lock();
            try {
                if (_threadMap == null) {
                    _threadMap = FactoryManager.getGlobal().newThreadLocal(SolonMDCAdapter.class, false);
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return _threadMap;
    }

    private static ThreadLocal<Map<String, Deque<String>>> threadMapOfStacks() {
        if (_threadMapOfStacks == null) {
            Utils.locker().lock();
            try {
                if (_threadMapOfStacks == null) {
                    _threadMapOfStacks = FactoryManager.getGlobal().newThreadLocal(SolonMDCAdapter.class, false);
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return _threadMapOfStacks;
    }


    @Override
    public void put(String key, String val) {
        Map<String, String> ht = threadMap().get();
        if (ht == null) {
            ht = new LinkedHashMap<>();
            threadMap().set(ht);
        }

        ht.put(key, val);
    }

    @Override
    public String get(String key) {
        Map<String, String> ht = threadMap().get();
        if (ht != null) {
            return ht.get(key);
        } else {
            return null;
        }
    }

    @Override
    public void remove(String key) {
        Map<String, String> ht = threadMap().get();
        if (ht != null) {
            ht.remove(key);
        }
    }

    @Override
    public void clear() {
        threadMap().set(null);
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> map = threadMap().get();
        if (map != null) {
            return new LinkedHashMap<>(map);
        } else {
            return map;
        }
    }

    @Override
    public void setContextMap(Map<String, String> map) {
        threadMap().set(map);
    }

    @Override
    public void pushByKey(String key, String value) {
        if (key != null) {
            Map<String, Deque<String>> map = this.threadMapOfStacks().get();
            if (map == null) {
                map = new HashMap();
                this.threadMapOfStacks().set(map);
            }

            Deque<String> deque = (Deque) ((Map) map).get(key);
            if (deque == null) {
                deque = new ArrayDeque();
            }

            deque.push(value);
            map.put(key, deque);
        }
    }

    @Override
    public String popByKey(String key) {
        if (key == null) {
            return null;
        } else {
            Map<String, Deque<String>> map = threadMapOfStacks().get();
            if (map == null) {
                return null;
            } else {
                Deque<String> deque = map.get(key);
                return deque == null ? null : deque.pop();
            }
        }
    }

    @Override
    public Deque<String> getCopyOfDequeByKey(String key) {
        if (key == null) {
            return null;
        } else {
            Map<String, Deque<String>> map = threadMapOfStacks().get();
            if (map == null) {
                return null;
            } else {
                Deque<String> deque = map.get(key);
                return deque == null ? null : new ArrayDeque(deque);
            }
        }
    }

    @Override
    public void clearDequeByKey(String key) {
        if (key != null) {
            Map<String, Deque<String>> map = threadMapOfStacks().get();
            if (map != null) {
                Deque<String> deque = map.get(key);
                if (deque != null) {
                    deque.clear();
                }
            }
        }
    }
}
