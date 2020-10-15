package org.noear.solon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注释
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface XNote {
    String value() default "";
}
