package org.noear.solon.annotation;

import java.lang.annotation.*;
import java.lang.reflect.InvocationHandler;

@Inherited //要可继承
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XAround {
    Class<? extends InvocationHandler> value();
}
