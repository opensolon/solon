package org.noear.solon.cloud.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 云端任务
 *
 * @author noear
 * @since 1.3
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CloudJob {
    /**
     * 名称, 支持${xxx}配置
     * */
    @Note("name")
    String value();
    /**
     * 描述, 支持${xxx}配置
     * */
    String description() default "";

    /**
     * 时间表达式（具体由适配框架支持）
     * */
    @Note("或cron：支持7位（秒，分，时，日期ofM，月，星期ofW，年）； 或简配：s，m，h，d")
    String cron7x();
}
