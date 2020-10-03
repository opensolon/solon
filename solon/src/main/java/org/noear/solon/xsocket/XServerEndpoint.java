package org.noear.solon.xsocket;

import org.noear.solon.core.XMethod;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XServerEndpoint {
    String value() default "";
    XMethod method() default XMethod.ALL;
}
