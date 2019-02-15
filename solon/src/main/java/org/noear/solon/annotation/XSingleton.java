package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 单例
 * */
@Inherited //要可继承
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XSingleton {
    boolean value() ;
}