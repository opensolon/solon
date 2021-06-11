package webapp.dso;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.validation.annotation.NoRepeatSubmitChecker;

/**
 * @author noear 2021/6/12 created
 */
@Component
public class NoRepeatSubmitCheckerImpl implements NoRepeatSubmitChecker{

    @Inject
    CacheService cache;

    @Override
    public boolean check(String key, int seconds) {
        String key2 = "lock." + key;

        synchronized (key2.intern()) {
            Object tmp = cache.get(key2);

            if (tmp == null) {
                //如果还没有，则锁成功
                cache.store(key2, "_", seconds);
                return true;
            } else {
                return false;
            }
        }
    }
}
