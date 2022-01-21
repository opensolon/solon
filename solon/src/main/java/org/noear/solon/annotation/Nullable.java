package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 可以为null
 * @author noear
 * @since 1.5
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Nullable {
}