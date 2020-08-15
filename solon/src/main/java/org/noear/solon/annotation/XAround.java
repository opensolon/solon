package org.noear.solon.annotation;

import java.lang.annotation.*;
import java.lang.reflect.InvocationHandler;

/**
 * 触发器：围绕处理（替代处理）
 * */
@Inherited //要可继承
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XAround {
    Class<? extends InvocationHandler> value();
}
