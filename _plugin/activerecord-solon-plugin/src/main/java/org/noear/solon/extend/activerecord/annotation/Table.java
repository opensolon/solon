package org.noear.solon.extend.activerecord.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.4
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String value(); //别名
}

