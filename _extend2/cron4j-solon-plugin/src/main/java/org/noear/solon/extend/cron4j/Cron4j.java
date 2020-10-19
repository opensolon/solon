package org.noear.solon.extend.cron4j;

import org.noear.solon.annotation.XNote;

import java.lang.annotation.*;

/**
 * Cron4j 任务注解，支持：java.lang.Runnable 或 Task接口
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cron4j {
    @XNote("cronx = 或cron：支持5位（分，时，天，月，周） 或速配：100ms,2s,1m,1h,1d(ms:毫秒；s:秒；m:分；h:小时；d:天)")
    String cron5x();
    boolean enable() default true;
    String name() default "";
}

