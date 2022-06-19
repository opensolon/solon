package org.noear.solon.extend.sessionstate.local;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 定时存储器（做为Session存储方案）
 * */
class ScheduledStore {
    private int _defaultSeconds;

    private Map<String, Entity> _data = new ConcurrentHashMap<>();   //缓存存储器
    private static ScheduledExecutorService _exec = Executors.newSingleThreadScheduledExecutor(); //线程池

    public ScheduledStore(int seconds){
        _defaultSeconds = seconds;
    }

    public Collection<String> keys(){
        return _data.keySet();
    }

    public void put(String block, String key, Object obj) {
        synchronized (block.intern()) {
            Entity ent = _data.get(block);
            if (ent == null) {
                ent = new Entity();
                _data.put(block, ent);
            } else {
                ent.futureDel();
            }

            ent.map.put(key, obj);

            ent.future = _exec.schedule(() -> {
                _data.remove(block);
            }, _defaultSeconds, TimeUnit.SECONDS);
        }
    }

    public void delay(String block) {
        Entity ent = _data.get(block);
        if (ent != null) {
            ent.futureDel();

            ent.future = _exec.schedule(() -> {
                _data.remove(block);
            }, _defaultSeconds, TimeUnit.SECONDS);
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
        synchronized (block.intern()) {
            Entity ent = _data.get(block);
            if (ent != null) {
                ent.map.remove(key);
            }
        }
    }

    public void clear(String block) {
        synchronized (block.intern()) {
            Entity ent = _data.get(block);
            if (ent != null) {
                ent.futureDel();

                _data.remove(block);
            }
        }
    }

    public void clear() {
        for (Entity ent : _data.values()) {
            ent.futureDel();
        }

        _data.clear();
    }

    //存储实体
    private static class Entity {
        public Map<String,Object> map = new ConcurrentHashMap<>();
        public Future future;

        protected void futureDel(){
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        }
    }
}