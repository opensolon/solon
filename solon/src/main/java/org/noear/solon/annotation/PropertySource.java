package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PropertySource {
    String[] value();
}