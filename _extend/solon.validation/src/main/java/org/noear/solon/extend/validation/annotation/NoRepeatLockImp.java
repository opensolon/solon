package org.noear.solon.extend.validation.annotation;

import org.noear.solon.core.Aop;
import org.noear.solon.core.cache.CacheService;

/**
 * 锁的默认实现
 *
 * 只适合本地锁；分布式环境需要把锁实现换掉
 * @author noear
 * @since 1.0
 * */
public class NoRepeatLockImp implements NoRepeatLock {
    private static NoRepeatLock _global;

    public static NoRepeatLock global() {
        if (_global == null) {
            _global = new NoRepeatLockImp();
        }

        return _global;
    }

    public static void globalSet(NoRepeatLock lock) {
        if (lock != null) {
            _global = lock;
        }
    }

    public boolean tryLock(String key, int inSeconds) {
        CacheService _cache = Aop.context().getBean(CacheService.class);

        if(_cache == null){
            throw new RuntimeException("There is no default CacheService");
        }

        String key2 = "lock." + key;

        synchronized (key2.intern()) {
            Object tmp = _cache.get(key2);

            if (tmp == null) {
                //如果还没有，则锁成功
                _cache.store(key2, "_", inSeconds);
                return true;
            } else {
                return false;
            }
        }
    }
}
