package io.seata.solon.annotation;

import java.lang.annotation.*;

/**
 * 全局锁
 *
 * @author noear
 * @since 2.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Inherited
public @interface GlobalLock {
    /**
     * customized global lock retry interval(unit: ms)
     * you may use this to override global config of "client.rm.lock.retryInterval"
     * note: 0 or negative number will take no effect(which mean fall back to global config)
     * @return lock retry interval
     */
    int lockRetryInterval() default 0;

    /**
     * customized global lock retry times
     * you may use this to override global config of "client.rm.lock.retryTimes"
     * note: negative number will take no effect(which mean fall back to global config)
     * @return lock retry times
     */
    int lockRetryTimes() default -1;
}
