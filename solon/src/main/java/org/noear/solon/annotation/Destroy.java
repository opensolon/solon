package org.noear.solon.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注销
 *
 * @author noear
 * @since 2.8
 */
@Retention(RUNTIME)
@Target(METHOD)
@Documented
public @interface Destroy {
}
