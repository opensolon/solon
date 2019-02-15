package org.noear.solon.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XInterceptor {
    boolean before() default false;
    boolean after() default false;
}

