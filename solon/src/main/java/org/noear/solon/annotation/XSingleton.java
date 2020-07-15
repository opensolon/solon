package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 单例
 *
 * 一般附加在XController上
 * */
@Inherited //要可继承
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XSingleton {
    boolean value() ;
}