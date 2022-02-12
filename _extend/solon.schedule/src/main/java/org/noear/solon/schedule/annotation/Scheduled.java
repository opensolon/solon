package org.noear.solon.schedule.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定时任务注解
 *
 * @author noear
 * @since 1.6
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {
    /**
     * 任务名称
     */
    String name() default "";

    /**
     * cron 表达式
     */
    @Note("支持7位（秒，分，时，日期ofM，月，星期ofW，年）")
    String cron() default "";

    /**
     * 时区
     */
    String zone() default "";

    /**
     * 固定间隔毫秒数
     */
    long fixedRate() default 0;

    /**
     * 固定延时毫秒数
     */
    long fixedDelay() default 0;

    /**
     * 可并发执行
     * */
    boolean concurrent() default true;
}
