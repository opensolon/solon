package org.noear.solon.extend.data.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCaching {
    boolean value() default true;
}
