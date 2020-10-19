package org.noear.solon.extend.cron4j;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Job {
    /**
     * cron4x = cron4 or 100ms,2s,1m,1h,1d(ms:毫秒；s:秒；m:分；h:小时；d:天)
     * */
    String cron4x();
    boolean enable() default true;
    String name() default "";
}

