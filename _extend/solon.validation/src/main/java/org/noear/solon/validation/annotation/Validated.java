package org.noear.solon.validation.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.3
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validated {
    Class<?>[] value() default {};
}
