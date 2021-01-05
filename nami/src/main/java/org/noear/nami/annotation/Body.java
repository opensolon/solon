package org.noear.nami.annotation;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.2
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {
}
