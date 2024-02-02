package org.noear.solon.scheduling.annotation;

import java.lang.annotation.*;

/**
 * 启用命令调度注解
 *
 * @author noear
 * @since 2.7
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCommand {
}
