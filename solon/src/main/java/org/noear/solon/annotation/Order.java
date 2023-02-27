package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 排序
 *
 * @author noear
 * @since 2.2
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Order {
     int value();
}
