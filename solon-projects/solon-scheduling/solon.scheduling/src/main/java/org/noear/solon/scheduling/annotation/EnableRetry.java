package org.noear.solon.scheduling.annotation;


import java.lang.annotation.*;

/**
 * 启用重试执行注解
 *
 * @author kongweiguang
 * @since 2.3
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableRetry {
}
