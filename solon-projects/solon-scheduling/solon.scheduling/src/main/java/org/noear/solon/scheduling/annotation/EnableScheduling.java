package org.noear.solon.scheduling.annotation;

import java.lang.annotation.*;

/**
 * 启用定时调度注解
 *
 * @author noear
 * @since 1.6
 * */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableScheduling {
}
