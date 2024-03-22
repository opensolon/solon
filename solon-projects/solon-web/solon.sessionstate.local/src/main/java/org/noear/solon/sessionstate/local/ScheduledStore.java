package org.noear.solon.sessionstate.local;

import org.noear.solon.core.util.RunUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 定时存储器（做为Session存储方案）
 * */
class ScheduledStore {
    private int _defaultSeconds;

    private Map<String, Entity> _data = new HashMap<>();   //缓存存储器
    private ReentrantLock SYNC_LOCK = new ReentrantLock();

    public ScheduledStore(int seconds) {
        _defaultSeconds = seconds;
    }

    public Collection<String> keys() {
        return _data.keySet();
    }

    public void put(String block, String key, Object obj) {
        SYNC_LOCK.lock();

        try {
            Entity ent = _data.get(block);
            if (ent == null) {
                ent = new Entity();
                _data.put(block, ent);
            } else {
                ent.futureDel();
            }

            ent.map.put(key, obj);

            ent.future = RunUtil.delay(() -> {
                _data.remove(block);
            }, _defaultSeconds * 1000);
        } finally {
            SYNC_LOCK.unlock();
        }
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

    public void remove(String block, String key) {
        SYNC_LOCK.lock();

        try {
            Entity ent = _data.get(block);
            if (ent != null) {
                ent.map.remove(key);
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    public void clear(String block) {
        SYNC_LOCK.lock();
        try {
            Entity ent = _data.get(block);
            if (ent != null) {
                ent.futureDel();

                _data.remove(block);
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }

    public void clear() {
        SYNC_LOCK.lock();
        try {
            for (Entity ent : _data.values()) {
                ent.futureDel();
            }

            _data.clear();
        } finally {
            SYNC_LOCK.unlock();
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