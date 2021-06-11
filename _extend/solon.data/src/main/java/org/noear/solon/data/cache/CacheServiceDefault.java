package org.noear.solon.data.cache;

import java.util.Map;
import java.util.concurrent.*;

public class CacheServiceDefault implements CacheService {
    public static CacheService instance = new CacheServiceDefault();

    private int _defaultSeconds;

    //缓存存储器
    private Map<String, Entity> _data = new ConcurrentHashMap<>();
    //计划线程池（用于超时处理）
    private static ScheduledExecutorService _exec = Executors.newSingleThreadScheduledExecutor();

    public CacheServiceDefault() {
        this(300);
    }

    public CacheServiceDefault(int defSeconds) {
        _defaultSeconds = defSeconds;
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        if(seconds == 0){
            seconds = getDefalutSeconds();
        }

        synchronized (key.intern()) {
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
                ent.future = _exec.schedule(() -> {
                    _data.remove(key);
                }, seconds, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public Object get(String key) {
        Entity ent = _data.get(key);

        return ent == null ? null : ent.value;
    }

    @Override
    public void remove(String key) {
        synchronized (key.intern()) {
            Entity ent = _data.remove(key);

            if (ent != null) {
                ent.futureDel();
            }
        }
    }

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
     * */
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
