package org.noear.solon.validation.annotation;

import org.noear.solon.annotation.Alias;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validated {
    @Alias("groups")
    Class<?>[] value() default {};
}
