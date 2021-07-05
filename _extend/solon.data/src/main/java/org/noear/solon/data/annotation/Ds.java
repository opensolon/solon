package org.noear.solon.data.annotation;

import java.lang.annotation.*;

/**
 * 数据源注解
 *
 * @author noear
 * @since 1.5
 * */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ds {
    /**
     * DataSource bean name
     * */
    String value() default "";
}
