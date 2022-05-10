package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 做为 @Mapping 的副词用
 *
 * @author noear
 * @since 1.7
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Consumes {
    String value();
}
