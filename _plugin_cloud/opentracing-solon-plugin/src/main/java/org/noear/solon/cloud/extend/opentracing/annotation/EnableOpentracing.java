package org.noear.solon.cloud.extend.opentracing.annotation;

import org.noear.solon.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear
 * @since 1.4
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableOpentracing {
}
