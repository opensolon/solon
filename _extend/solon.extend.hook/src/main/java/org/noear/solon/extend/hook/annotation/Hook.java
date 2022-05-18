package org.noear.solon.extend.hook.annotation;

import org.noear.solon.annotation.Around;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author noear
 * @since 1.8
 */
@Around(HookInterceptor.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Hook {
    String value();
}
