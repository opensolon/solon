package org.noear.solon.schedule.annotation;

import org.noear.solon.annotation.Note;

import java.lang.annotation.*;

/**
 * 启用定时任务注解
 *
 * @author noear
 * @since 1.6
 * */
@Note("由插件 solon.scheduling.simple 替代")
@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableScheduling {
}
