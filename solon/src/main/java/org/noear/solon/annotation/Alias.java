package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 别名
 *
 * @author noear
 * @since 1.0
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Alias {
    String value();
}
