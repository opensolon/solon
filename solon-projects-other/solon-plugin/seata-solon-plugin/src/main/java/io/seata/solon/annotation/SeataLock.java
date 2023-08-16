package io.seata.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear 2023/8/16 created
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SeataLock {

    int lockRetryInterval() default 0;

    int lockRetryTimes() default -1;

}
