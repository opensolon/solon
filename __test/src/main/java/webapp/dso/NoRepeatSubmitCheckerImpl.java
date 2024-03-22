package webapp.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.validation.annotation.NoRepeatSubmit;
import org.noear.solon.validation.annotation.NoRepeatSubmitChecker;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author noear 2021/6/12 created
 */
@Component
public class NoRepeatSubmitCheckerImpl implements NoRepeatSubmitChecker {

    @Inject
    CacheService cache;

    final ReentrantLock SYNC_LOCK = new ReentrantLock();

    @Override
    public boolean check(NoRepeatSubmit anno, Context ctx, String submitHash, int limitSeconds) {
        String key2 = "lock." + submitHash;

        SYNC_LOCK.lock();
        try {
            Object tmp = cache.get(key2, Object.class);

            if (tmp == null) {
                //如果还没有，则锁成功
                cache.store(key2, "_", limitSeconds);
                return true;
            } else {
                return false;
            }
        } finally {
            SYNC_LOCK.unlock();
        }
    }
}
