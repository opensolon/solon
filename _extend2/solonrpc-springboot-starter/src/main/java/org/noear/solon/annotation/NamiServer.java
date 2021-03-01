package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * @author noear 2021/3/1 created
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NamiServer {
    String mapping() default "";
}
