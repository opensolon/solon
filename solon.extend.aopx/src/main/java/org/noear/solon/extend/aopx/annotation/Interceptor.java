package org.noear.solon.extend.aopx.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Interceptor {
    boolean before() default false;
    boolean after() default false;
}

