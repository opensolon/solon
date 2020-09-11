package org.noear.solon.test;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SolonTest {
    Class<?> value();
    boolean debug() default true;
}
