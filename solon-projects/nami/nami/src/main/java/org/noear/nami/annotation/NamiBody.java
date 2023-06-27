package org.noear.nami.annotation;

import org.noear.nami.common.ContentTypes;

import java.lang.annotation.*;

/**
 * 指定参数转为Body
 *
 * @author noear
 * @since 1.2
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NamiBody {
    String contentType() default ContentTypes.JSON_VALUE;
}
