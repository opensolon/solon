package org.noear.solon.socket;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XSignalEndpoint {
    String value() default "";
}
