package org.noear.solon.extend.sessionstate.local;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

class ScheduledStore {
    private int _defaultSeconds;

    private Map<String, Entity> _data = new ConcurrentHashMap<>();   //缓存存储器
    private static ScheduledExecutorService _exec = Executors.newSingleThreadScheduledExecutor(); //线程池

    public ScheduledStore(int seconds){
        _defaultSeconds = seconds;
    }

    public void put(String block, String key, Object obj) {
        synchronized (block.intern()) {
            Entity entity = _data.get(block);
            if (entity == null) {
                entity = new Entity();
                _data.put(block, entity);
            } else {
                if (entity.future != null) {
                    entity.future.cancel(true);
                    entity.future = null;
                }
            }

            entity.map.put(key, obj);

            entity.future = _exec.schedule(() -> {
                _data.remove(block);
            }, _defaultSeconds, TimeUnit.SECONDS);
        }
    }

    public void delay(String block) {
        Entity entity = _data.get(block);
        if (entity != null) {
            if (entity.future != null) {
                entity.future.cancel(true);
            }

            entity.future = _exec.schedule(() -> {
                _data.remove(block);
            }, _defaultSeconds, TimeUnit.SECONDS);
        }
    }

    public Object get(String block, String key) {
        Entity entity = _data.get(block);
        if (entity != null) {
            return entity.map.get(key);
        }

        return null;
    }

    public void remove(String block) {
        synchronized (block.intern()) {
            Entity entity = _data.get(block);
            if (entity != null) {
                if (entity.future != null) {
                    entity.future.cancel(true);
                    entity.future = null;
                }

                _data.remove(block);
            }
        }
    }

    public void clear() {
        for (Entity val : _data.values()) {
            if (val.future != null) {
                val.future.cancel(true);
                val.future = null;
            }
        }

        _data.clear();
    }

    //存储实体
    private static class Entity {
        public Map<String,Object> map = new HashMap<>();
        public Future future;
    }
}
