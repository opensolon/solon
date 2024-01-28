package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 间接附加注解
 *
 * @author noear
 * @since 2.6
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Addition {
    Class<? extends Annotation>[] value();
}
