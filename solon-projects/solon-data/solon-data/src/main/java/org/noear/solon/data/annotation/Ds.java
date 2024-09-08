package org.noear.solon.data.annotation;

import java.lang.annotation.*;

/**
 * 数据源注解
 *
 * @author noear
 * @since 2.9
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ds {
    /**
     * ds name
     */
    String value() default "";
}