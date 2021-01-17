package org.noear.nami.annotation;

import org.noear.nami.common.Constants;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.2
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {
    String contentType() default Constants.CONTENT_TYPE_JSON;
}
