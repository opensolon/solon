package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 注释
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Note {
    String value();
}
