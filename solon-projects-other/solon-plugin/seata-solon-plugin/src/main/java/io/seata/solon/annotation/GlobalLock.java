package io.seata.solon.annotation;

import java.lang.annotation.*;

/**
 * 全局锁
 *
 * @author noear
 * @since 2.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface GlobalLock {

    int lockRetryInterval() default 0;

    int lockRetryTimes() default -1;

}
