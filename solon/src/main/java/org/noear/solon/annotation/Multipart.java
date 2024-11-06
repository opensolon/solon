package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 做为 @Mapping 的副词用，申明支持多部分
 *
 * @author noear
 * @since 3.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Multipart {
}
